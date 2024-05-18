package com.niobe.can_i.usecases.camarero_menu.camarero_home.cesta

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityCestaBinding
import com.niobe.can_i.model.ArticulosComanda
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.camarero_menu.camarero_home.cesta.sel_articulo_cesta.SelArticuloCestaActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util

class CestaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCestaBinding
    private lateinit var firebaseUtil: FirebaseUtil
    private lateinit var adapter: CestaAdapter
    private var idComanda: String? = null
    private var idCamarero: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCestaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializamos FirebaseUtil
        firebaseUtil = FirebaseUtil()

        initUI()
    }

    override fun onResume() {
        super.onResume()
        // Recargar la lista de artículos cada vez que la actividad se reanuda
        idComanda?.let { loadArticulosComanda(it) }
    }

    private fun initUI() {
        idComanda = intent.getStringExtra(Constants.EXTRA_COMANDA)
        Log.i("IDCOMANDA CESTA", idComanda ?: "")
        idCamarero = intent.getStringExtra(Constants.EXTRA_USUARIO)
        Log.i("IDCAMARERO CESTA", idCamarero ?: "")

        if (idComanda.isNullOrEmpty()) {
            Log.e("Error", "El idComanda es inválido o está vacío")
            finish()
            return
        }

        setupRecyclerView()
        loadArticulosComanda(idComanda!!)

        binding.ivBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = CestaAdapter { articuloId -> navigateToDetail(articuloId) }
        Util.setupRecyclerViewVertical(this, binding.rvCesta, adapter)
    }

    private fun loadArticulosComanda(idComanda: String) {
        firebaseUtil.getArticulosComandaByComanda(idComanda) { articulosComandaList ->
            adapter.updateList(articulosComandaList)
            showTotalPrice(articulosComandaList) // Llama a la función showTotalPrice después de actualizar la lista de artículos
        }
    }

    private fun showTotalPrice(articulosComandaList: List<ArticulosComanda>) {
        var totalPrice = 0.0
        var loadedArticulos = 0 // Para llevar el conteo de cuántos artículos se han cargado

        // Recorre la lista de artículos de la cesta
        for (articuloComanda in articulosComandaList) {
            // Obtiene el artículo correspondiente al ID del artículo de la cesta
            firebaseUtil.getArticuloById(articuloComanda.idArticulo.articuloId) { articulo ->
                // Verifica si se encontró el artículo y si tiene un precio válido
                if (articulo != null && articulo.precio > 0) {
                    // Incrementa el total sumando el precio del artículo multiplicado por la cantidad
                    totalPrice += articulo.precio * articuloComanda.cantidad
                }
                // Incrementa el contador de artículos cargados
                loadedArticulos++

                // Comprueba si ya se han cargado todos los artículos
                if (loadedArticulos == articulosComandaList.size) {
                    // Actualiza el TextView con el precio total
                    binding.tvTotalPrecio.text = buildString {
                        append(totalPrice.toString())
                        append("€")
                    }
                }
            }
        }
    }

    private fun navigateToDetail(idArticulo: String) {
        val intent = Intent(this, SelArticuloCestaActivity::class.java)
        intent.putExtra(Constants.EXTRA_ID, idArticulo)
        intent.putExtra(Constants.EXTRA_COMANDA, idComanda)
        intent.putExtra(Constants.EXTRA_USUARIO, idCamarero)
        startActivity(intent)
    }
}
