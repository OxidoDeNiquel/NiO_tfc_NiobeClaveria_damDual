package com.niobe.can_i.usecases.admin_menu.admin_articulos.admin_articulos_lista

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityListaArticulosBinding
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.admin_menu.admin_articulos.sel_articulo.SelArticuloActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util
import androidx.appcompat.widget.SearchView

class ListaArticulosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaArticulosBinding
    private val firebaseUtil = FirebaseUtil()
    private lateinit var adapter: ListaArticulosAdapter
    private lateinit var tipoArticulo: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListaArticulosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUI()
        setupSearchView()
    }

    private fun initUI() {
        tipoArticulo = intent.getStringExtra(Constants.EXTRA_TIPO_ARTICULO).orEmpty()
        if (tipoArticulo.isNotEmpty()) {
            actualizarRecyclerViews(tipoArticulo)
        } else {
            Log.e("ListaArticulosActivity", "Tipo de artículo no proporcionado")
        }

        binding.tvTipoBebida.text = tipoArticulo

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun actualizarRecyclerViews(tipoArticulo: String) {
        val recyclerView = binding.rvArticulos
        adapter = ListaArticulosAdapter { articuloId -> navigateToDetail(articuloId) }
        Util.setupRecyclerViewVertical(this, recyclerView, adapter)

        firebaseUtil.leerArticulos(tipoArticulo) { articulos ->
            adapter.updateList(articulos)
        }
    }

    private fun navigateToDetail(id: String) {
        val intent = Intent(this, SelArticuloActivity::class.java)
        intent.putExtra(Constants.EXTRA_ID, id)
        startActivity(intent)
    }

    private fun setupSearchView() {
        val searchView = binding.searchBar
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchArticulos(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchArticulos(it)
                }
                return false
            }
        })
    }

    private fun searchArticulos(query: String) {
        firebaseUtil.buscarArticulosPorTipoYNombre(tipoArticulo, query) { articulos ->
            adapter.updateList(articulos)
        }
    }
}
