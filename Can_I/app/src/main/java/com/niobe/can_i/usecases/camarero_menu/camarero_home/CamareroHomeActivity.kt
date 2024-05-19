package com.niobe.can_i.usecases.camarero_menu.camarero_home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityCamareroHomeBinding
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.camarero_menu.camarero_home.cesta.CestaActivity
import com.niobe.can_i.usecases.camarero_menu.camarero_home.lista_articulos.ListaArticulosActivity
import com.niobe.can_i.usecases.camarero_menu.camarero_home.sel_articulo.SelArticuloCamareroActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util

class CamareroHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCamareroHomeBinding
    private lateinit var firebaseUtil: FirebaseUtil
    private var idComanda: String? = null
    private var idCamarero: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCamareroHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainView = findViewById<View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // Inicializamos FirebaseUtil
        firebaseUtil = FirebaseUtil()

        initUI()
    }

    private fun initUI() {
        idComanda = intent.getStringExtra(Constants.EXTRA_COMANDA)
        idCamarero = intent.getStringExtra(Constants.EXTRA_USUARIO)

        if (idComanda.isNullOrEmpty()) {
            Log.e("Error idComanda", "El idComanda es inválido")
            finish()
            return
        }

        binding.ivCerveza.setOnClickListener {
            navigateToList(Constants.TIPO_ARTICULO_CERVEZA)
        }
        binding.ivCopa.setOnClickListener {
            navigateToList(Constants.TIPO_ARTICULO_COPA)
        }
        binding.ivSinAlcohol.setOnClickListener {
            navigateToList(Constants.TIPO_ARTICULO_SIN_ALCOHOL)
        }
        binding.tvInicio.setOnClickListener {
            finish()
        }
        binding.bCesta.setOnClickListener {
            navigateToCesta()
        }

        // Actualizar RecyclerViews
        actualizarRecyclerViews()
    }

    override fun onResume() {
        super.onResume()
        actualizarRecyclerViews()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_CODE_CREAR_ARTICULO && resultCode == Activity.RESULT_OK) {
            val articuloId = data?.getStringExtra("articuloId")
            if (articuloId != null) {
                actualizarRecyclerViews()
            }
        }
    }

    private fun actualizarRecyclerViews() {
        leerArticulos(Constants.TIPO_ARTICULO_CERVEZA, binding.rvCervezas)
        leerArticulos(Constants.TIPO_ARTICULO_COPA, binding.rvCopa)
        leerArticulos(Constants.TIPO_ARTICULO_SIN_ALCOHOL, binding.rvSinAlcohol)
    }

    private fun leerArticulos(tipoArticulo: String, recyclerView: RecyclerView) {
        firebaseUtil.leerArticulos(tipoArticulo) { articulos ->
            val adapter = CamareroHomeAdapter { articuloId -> navigateToDetail(articuloId) }
            Util.setupRecyclerViewHorizontal(this@CamareroHomeActivity, recyclerView, adapter)
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

    private fun navigateToList(tipoArticulo: String) {
        val intent = Intent(this, ListaArticulosActivity::class.java)
        intent.putExtra(Constants.EXTRA_TIPO_ARTICULO, tipoArticulo)
        intent.putExtra(Constants.EXTRA_COMANDA, idComanda)
        intent.putExtra(Constants.EXTRA_USUARIO, idCamarero)
        startActivity(intent)
    }

    private fun navigateToCesta() {
        val intent = Intent(this, CestaActivity::class.java)
        intent.putExtra(Constants.EXTRA_COMANDA, idComanda)
        intent.putExtra(Constants.EXTRA_USUARIO, idCamarero)
        startActivity(intent)
    }
}
