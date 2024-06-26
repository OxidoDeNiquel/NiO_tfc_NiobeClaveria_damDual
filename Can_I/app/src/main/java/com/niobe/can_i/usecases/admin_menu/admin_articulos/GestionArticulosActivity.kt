package com.niobe.can_i.usecases.admin_menu.admin_articulos

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityGestionArticulosBinding
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.admin_menu.admin_articulos.admin_articulos_lista.ListaArticulosActivity
import com.niobe.can_i.usecases.admin_menu.admin_articulos.crear_articulo.CrearArticuloActivity
import com.niobe.can_i.usecases.admin_menu.admin_articulos.sel_articulo.SelArticuloActivity
import com.niobe.can_i.usecases.perfil.PerfilActivity
import com.niobe.can_i.usecases.camarero_menu.camarero_home.CamareroHomeActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util

class GestionArticulosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGestionArticulosBinding
    private lateinit var firebaseUtil: FirebaseUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Inicializamos el binding
        binding = ActivityGestionArticulosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializamos FirebaseUtil
        firebaseUtil = FirebaseUtil()

        // Inicializamos los componentes
        initUI()
    }

    // Función para inicializar todos los componentes del layout
    private fun initUI() {
        val uidAuth = intent.getStringExtra(Constants.EXTRA_USUARIO)

        if(uidAuth != null){
            binding.tvPerfil.setOnClickListener {
                navigateToProfile(uidAuth)
            }
        }
        
        // Configuramos el click listener para el botón
        binding.bAnadirArticulo.setOnClickListener {
            navigateToAddArticle()
        }
        binding.tvInicio.setOnClickListener {
            finish()
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
            val adapter = GestionArticulosAdapter { articuloId -> navigateToDetail(articuloId) }
            Util.setupRecyclerViewHorizontal(this@GestionArticulosActivity, recyclerView, adapter)
            adapter.updateList(articulos)
        }
    }

    private fun navigateToProfile(uidAuth: String) {
        val intent = Intent(this, PerfilActivity::class.java)
        intent.putExtra(Constants.EXTRA_USUARIO, uidAuth)
        startActivity(intent)
    }

    private fun navigateToAddArticle() {
        val intent = Intent(this, CrearArticuloActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToDetail(id: String) {
        val intent = Intent(this, SelArticuloActivity::class.java)
        intent.putExtra(Constants.EXTRA_ID, id)
        startActivity(intent)
    }

    private fun navigateToList(tipoArticulo: String) {
        val intent = Intent(this, ListaArticulosActivity::class.java)
        intent.putExtra(Constants.EXTRA_TIPO_ARTICULO, tipoArticulo)
        startActivity(intent)
    }
}
