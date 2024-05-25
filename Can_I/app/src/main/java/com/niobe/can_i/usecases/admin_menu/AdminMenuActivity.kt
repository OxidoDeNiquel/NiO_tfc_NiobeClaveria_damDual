package com.niobe.can_i.usecases.admin_menu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityAdminMenuBinding
import com.niobe.can_i.model.Usuario
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.admin_menu.admin_articulos.GestionArticulosActivity
import com.niobe.can_i.usecases.admin_menu.admin_usuarios.GestionUsuariosActivity
import com.niobe.can_i.usecases.admin_menu.admin_usuarios.GestionUsuariosAdapter
import com.niobe.can_i.usecases.admin_menu.admin_usuarios.crear_usuario.CrearUsuarioActivity
import com.niobe.can_i.usecases.camarero_menu.camarero_home.cesta.CestaActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util

class AdminMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminMenuBinding
    private lateinit var firebaseUtil: FirebaseUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //Inicializamos el binding
        binding = ActivityAdminMenuBinding.inflate(layoutInflater)

        //Asignamos el contentView al binding
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Inicializamos Firebase
        firebaseUtil = FirebaseUtil()
        //Inicializamos los componentes
        initUI()
    }
    //Función para inicializar todos los componentes del layout
    private fun initUI(){
        val uidAuth = intent.getStringExtra(Constants.EXTRA_USUARIO)

        if(uidAuth != null){
            Log.i("uidAuth", uidAuth)
            getUsuarioInformacion(uidAuth)
            // Configuramos el click listener para el botón
            binding.bGestionArticulos.setOnClickListener {
                // Aquí se ejecutará cuando se presione el botón
                navigateToGestionArticulos(uidAuth)
            }
            binding.bGestionEmpleados.setOnClickListener {
                navigateToGestionUsuarios(uidAuth)
            }
            binding.bCerrarSesion.setOnClickListener {
                firebaseUtil.cerrarSesion(this)
            }
            /*binding.bGestionIncidencias.setOnClickListener {
                Util.changeActivity(this, GestionBarrasActivity::class.java)
            }*/
        }else{
            Log.e("Error uidAuth", "El uid es inválido")
        }

    }

    private fun navigateToGestionArticulos(uidAuth: String) {
        val intent = Intent(this, GestionArticulosActivity::class.java)
        intent.putExtra(Constants.EXTRA_USUARIO, uidAuth)
        startActivity(intent)
    }

    private fun navigateToGestionUsuarios(uidAuth: String) {
        val intent = Intent(this, GestionUsuariosActivity::class.java)
        intent.putExtra(Constants.EXTRA_USUARIO, uidAuth)
        startActivity(intent)
    }

    private fun getUsuarioInformacion(idUsuario: String) {
        firebaseUtil.obtenerUsuarioPorId(idUsuario,
            onSuccess = { usuario ->
                if (usuario != null) {
                    // Manejar el éxito, por ejemplo, mostrar los datos del usuario en la interfaz de usuario
                    createUI(usuario)
                } else {
                    // El usuario no existe
                    Util.showToast(this, "El usuario no existe")
                }
            },
            onFailure = { exception ->
                // Manejar el error, por ejemplo, mostrar un mensaje de error al usuario
                Util.showToast(this, "Error al obtener usuario: $exception")
            }
        )
    }

    private fun createUI(usuario: Usuario){
        binding.tvNombreAdmin.text = buildString {
            append(usuario.nombre)
            append(" ")
            append(usuario.apellido1)
        }
    }
}