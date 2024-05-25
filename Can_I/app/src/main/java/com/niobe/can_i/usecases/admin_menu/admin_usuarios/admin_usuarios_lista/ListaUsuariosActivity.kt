package com.niobe.can_i.usecases.admin_menu.admin_usuarios.admin_usuarios_lista

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityListaUsuariosBinding
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.admin_menu.admin_articulos.admin_articulos_lista.ListaArticulosAdapter
import com.niobe.can_i.usecases.admin_menu.admin_usuarios.sel_usuario.SelUsuarioActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util

class ListaUsuariosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaUsuariosBinding
    private lateinit var adapter: ListaUsuariosAdapter
    private lateinit var rol: String
    private val firebaseUtil = FirebaseUtil()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListaUsuariosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUI()
        setupSearchView()
    }

    private fun initUI(){
        rol = intent.getStringExtra(Constants.EXTRA_USUARIO) ?: ""
        if (rol != null) {
            actualizarRecyclerViews(rol)
        } else {
            // Manejar el caso en que no se proporcionó el tipo de artículo
            Log.e("ListaUsuarioActivity", "Rol de usuario no proporcionado")
        }

        binding.tvRolUsuario.text = rol

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun actualizarRecyclerViews(rol: String) {
        val recyclerView = binding.rvUsuarios
        // Aquí, en lugar de crear una nueva variable local `adapter`, inicializa la variable de clase `adapter`.
        adapter = ListaUsuariosAdapter { articuloId -> navigateToDetail(articuloId) }
        Util.setupRecyclerViewVertical(this, recyclerView, adapter)

        firebaseUtil.leerUsuariosPorRol(rol) { usuarios ->
            adapter.updateList(usuarios)
        }
    }


    private fun navigateToDetail(id: String) {
        val intent = Intent(this, SelUsuarioActivity::class.java)
        intent.putExtra(Constants.EXTRA_ID, id)
        startActivity(intent)
    }

    private fun setupSearchView() {
        val searchView = binding.searchBar
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchUsuarios(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchUsuarios(it)
                }
                return false
            }
        })
    }

    private fun searchUsuarios(query: String) {
        firebaseUtil.buscarUsuariosPorRolYNombre(rol, query) { usuarios ->
            adapter.updateList(usuarios)
        }
    }
}