package com.niobe.can_i.usecases.admin_menu.admin_usuarios.sel_usuario

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivitySelUsuarioBinding
import com.niobe.can_i.model.Usuario
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.admin_menu.admin_usuarios.sel_usuario.editar_usuario.EditarUsuarioActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util

class SelUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelUsuarioBinding
    private lateinit var firebaseUtil: FirebaseUtil
    private var idUsuario: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySelUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseUtil = FirebaseUtil()

        idUsuario = intent.getStringExtra(Constants.EXTRA_ID)

        initUI()
    }

    override fun onResume() {
        super.onResume()
        idUsuario?.let { getUsuarioInformacion(it) }
    }

    private fun initUI() {
        if (idUsuario != null) {
            binding.ivBack.setOnClickListener {
                finish()
            }
            binding.bEditarUsuario.setOnClickListener {
                navigateToEditUsuario(idUsuario!!)
            }
            binding.bBorrarUsuario.setOnClickListener {
                deleteUsuario(idUsuario!!)
            }
        } else {
            // Manejar el caso donde no se proporciona el ID del usuario
            Toast.makeText(this, "ID de usuario no válido", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun navigateToEditUsuario(idUsuario: String) {
        val intent = Intent(this, EditarUsuarioActivity::class.java)
        intent.putExtra(Constants.EXTRA_ID, idUsuario)
        startActivity(intent)
    }

    private fun deleteUsuario(idUsuario: String) {
        firebaseUtil.eliminarUsuario(idUsuario,
            onSuccess = {
                // Manejar el éxito, por ejemplo, actualizar la interfaz de usuario
                Util.showToast(this, "Usuario eliminado con éxito")
                finish()
            },
            onFailure = { exception ->
                // Manejar el error, por ejemplo, mostrar un mensaje de error al usuario
                Util.showToast(this, "Error al eliminar usuario")
                finish()
            }
        )
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

    private fun createUI(usuario: Usuario) {
        binding.tvNombreUsuario.text = buildString {
            append(usuario.nombre)
            append(" ")
            append(usuario.apellido1)
        }
        binding.tvPuesto.text = buildString {
            append("Puesto: ")
            append(usuario.rol)
        }
        binding.tvEmail.text = buildString {
            append("Email: ")
            append(usuario.email)
        }
    }
}
