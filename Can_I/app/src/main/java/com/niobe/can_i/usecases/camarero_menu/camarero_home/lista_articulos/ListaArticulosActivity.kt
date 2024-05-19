package com.niobe.can_i.usecases.camarero_menu.camarero_home.lista_articulos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityListaArticulosBinding
import com.niobe.can_i.databinding.ActivityListaArticulosCamareroBinding
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.admin_menu.admin_articulos.GestionArticulosActivity
import com.niobe.can_i.usecases.admin_menu.admin_articulos.admin_articulos_lista.ListaArticulosAdapter
import com.niobe.can_i.usecases.admin_menu.admin_articulos.sel_articulo.SelArticuloActivity
import com.niobe.can_i.usecases.camarero_menu.camarero_home.CamareroHomeActivity
import com.niobe.can_i.usecases.camarero_menu.camarero_home.sel_articulo.SelArticuloCamareroActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util

class ListaArticulosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaArticulosCamareroBinding
    private val firebaseUtil = FirebaseUtil()
    private var idComanda: String? = null
    private var idCamarero: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListaArticulosCamareroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUI()
    }

    private fun initUI() {
        idComanda = intent.getStringExtra(Constants.EXTRA_COMANDA)
        idCamarero = intent.getStringExtra(Constants.EXTRA_USUARIO)
        val tipoArticulo = intent.getStringExtra(Constants.EXTRA_TIPO_ARTICULO)
        if (tipoArticulo != null) {
            actualizarRecyclerViews(tipoArticulo)
        } else {
            // Manejar el caso en que no se proporcionó el tipo de artículo
            Log.e("ListaArticulosActivity", "Tipo de artículo no proporcionado")
        }

        binding.tvTipoBebida.text = tipoArticulo

        binding.ivBack.setOnClickListener {
            Util.changeActivity(this, CamareroHomeActivity::class.java)
        }
    }

    private fun actualizarRecyclerViews(tipoArticulo: String) {
        val recyclerView = binding.rvArticulos
        val adapter = ListaArticulosAdapter { articuloId -> navigateToDetail(articuloId) }
        Util.setupRecyclerViewVertical(this, recyclerView, adapter)

        firebaseUtil.leerArticulos(tipoArticulo) { articulos ->
            adapter.updateList(articulos)
        }
    }

    private fun navigateToDetail(id: String) {
        val intent = Intent(this, SelArticuloCamareroActivity::class.java)
        intent.putExtra(Constants.EXTRA_ID, id)
        intent.putExtra(Constants.EXTRA_COMANDA, idComanda)
        intent.putExtra(Constants.EXTRA_USUARIO, idCamarero)
        startActivity(intent)
    }
}
