package com.niobe.can_i.usecases.admin_menu.admin_usuarios

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityGestionUsuariosBinding
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.admin_menu.admin_articulos.GestionArticulosAdapter
import com.niobe.can_i.usecases.admin_menu.admin_articulos.admin_articulos_lista.ListaArticulosActivity
import com.niobe.can_i.usecases.admin_menu.admin_articulos.sel_articulo.SelArticuloActivity
import com.niobe.can_i.usecases.admin_menu.admin_usuarios.admin_usuarios_lista.ListaUsuariosActivity
import com.niobe.can_i.usecases.admin_menu.admin_usuarios.crear_usuario.CrearUsuarioActivity
import com.niobe.can_i.usecases.admin_menu.admin_usuarios.sel_usuario.SelUsuarioActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util

class GestionUsuariosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGestionUsuariosBinding
    private lateinit var firebaseUtil: FirebaseUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGestionUsuariosBinding.inflate(layoutInflater)
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

    private fun initUI(){
        binding.tvInicio.setOnClickListener {
            finish()
        }
        binding.bAnadirUsuario.setOnClickListener {
            Util.changeActivityWithoutFinish(this, "", CrearUsuarioActivity::class.java)
        }
        binding.ivCamareros.setOnClickListener {
            navigateToList(Constants.TIPO_USUARIO_CAMARERO)
        }
        binding.ivAdministradores.setOnClickListener {
            navigateToList(Constants.TIPO_USUARIO_ADMINISTRADOR)
        }
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
            val idUsuario = data?.getStringExtra("articuloId")
            // Verificar que la clave del artículo no sea nula
            if (idUsuario != null) {
                // Actualizar los RecyclerViews utilizando la clave del artículo
                actualizarRecyclerViews()
            }
        }
    }

    private fun actualizarRecyclerViews() {
        leerUsuarios(Constants.TIPO_USUARIO_CAMARERO, binding.rvCamareros)
        leerUsuarios(Constants.TIPO_USUARIO_ADMINISTRADOR, binding.rvAdministradores)
    }

    private fun leerUsuarios(rol: String, recyclerView: RecyclerView) {
        firebaseUtil.leerUsuariosPorRol(rol) { usuarios ->
            val adapter = GestionUsuariosAdapter { usuarioId -> navigateToDetail(usuarioId) }
            Util.setupRecyclerViewVertical(this@GestionUsuariosActivity, recyclerView, adapter)
            adapter.updateList(usuarios)
        }
    }

    private fun navigateToDetail(id: String) {
        val intent = Intent(this, SelUsuarioActivity::class.java)
        intent.putExtra(Constants.EXTRA_ID, id)
        startActivity(intent)
    }

    private fun navigateToList(rol: String) {
        val intent = Intent(this, ListaUsuariosActivity::class.java)
        intent.putExtra(Constants.EXTRA_USUARIO, rol)
        startActivity(intent)
    }
}