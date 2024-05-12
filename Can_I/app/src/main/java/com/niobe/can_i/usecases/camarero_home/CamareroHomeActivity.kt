package com.niobe.can_i.usecases.camarero_home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityCamareroHomeBinding
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.camarero_home.sel_articulo.SelArticuloCamareroActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util

class CamareroHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCamareroHomeBinding
    private lateinit var firebaseUtil: FirebaseUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCamareroHomeBinding.inflate(layoutInflater)
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

    private fun initUI() {
        // Leer y mostrar los artículos por tipo en los RecyclerViews
        onResume()
    }

    override fun onResume() {
        super.onResume()
        actualizarRecyclerViews()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_CODE_CREAR_ARTICULO && resultCode == Activity.RESULT_OK) {
            // Obtener la clave del artículo del intent resultante
            val articuloId = data?.getStringExtra("articuloId")
            // Verificar que la clave del artículo no sea nula
            if (articuloId != null) {
                // Actualizar los RecyclerViews utilizando la clave del artículo
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
        startActivity(intent)
    }
}