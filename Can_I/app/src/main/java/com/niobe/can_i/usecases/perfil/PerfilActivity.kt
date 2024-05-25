package com.niobe.can_i.usecases.perfil

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityPerfilBinding
import com.niobe.can_i.model.Usuario
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.util.Constants

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private lateinit var firebaseUtil: FirebaseUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verificar que la vista con el ID main existe antes de usarla
        val mainView = findViewById<View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        } else {
            Log.e("PerfilActivity", "View with ID 'main' not found in the layout.")
        }

        // Inicializamos FirebaseUtil
        firebaseUtil = FirebaseUtil()

        // Inicializamos los componentes
        initUI()
    }

    private fun initUI() {
        val uidAuth = intent.getStringExtra(Constants.EXTRA_USUARIO)

        if (uidAuth != null) {
            // Llamada a la función obtenerUsuarioPorId
            firebaseUtil.obtenerUsuarioPorId(uidAuth,
                onSuccess = { usuario ->
                    if (usuario != null) {
                        createUI(usuario)
                        // Realiza aquí las acciones necesarias con el usuario
                        Log.d("Usuario", "Usuario encontrado: ${usuario.nombre}")
                    } else {
                        // El documento no existe
                        Log.d("Usuario", "El usuario no existe.")
                    }
                },
                onFailure = { exception ->
                    // Manejar la excepción
                    Log.e("Usuario", "Error al obtener el usuario: ", exception)
                }
            )
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun createUI(usuario: Usuario) {
        binding.tvUsuario.text = usuario.email
        binding.tvNombre.text = usuario.nombre
        binding.tvApellidos.text = buildString {
            append(usuario.apellido1)
            append(" ")
            append(usuario.apellido2)
        }
        binding.tvRol.text = usuario.rol
        binding.tvNombreAdmin.text = buildString {
            append(usuario.nombre)
            append(" ")
            append(usuario.apellido1)
        }
    }
}
