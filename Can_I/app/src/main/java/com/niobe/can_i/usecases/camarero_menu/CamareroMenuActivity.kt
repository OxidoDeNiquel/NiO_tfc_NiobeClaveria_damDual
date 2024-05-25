package com.niobe.can_i.usecases.camarero_menu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityAdminMenuBinding
import com.niobe.can_i.databinding.ActivityCamareroMenuBinding
import com.niobe.can_i.model.Usuario
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.admin_menu.admin_articulos.GestionArticulosActivity
import com.niobe.can_i.usecases.admin_menu.admin_usuarios.GestionUsuariosActivity
import com.niobe.can_i.usecases.camarero_menu.camarero_home.CamareroHomeActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util

class CamareroMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCamareroMenuBinding
    private lateinit var firebaseUtil: FirebaseUtil
    private lateinit var user: Usuario
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCamareroMenuBinding.inflate(layoutInflater)
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

    private fun initUI(){
        val uidAuth = intent.getStringExtra(Constants.EXTRA_USUARIO)

        if(uidAuth != null){
            getUsuarioInformacion(uidAuth)
            // Configuramos el click listener para el botón
            binding.bCrearComanda.setOnClickListener {
                firebaseUtil.obtenerCamareroPorId(uidAuth,
                    onSuccess = { camarero ->
                        if (camarero != null) {
                            firebaseUtil.crearComanda(camarero,
                                onSuccess = { idComanda ->
                                    val intent = Intent(this, CamareroHomeActivity::class.java)
                                    intent.putExtra(Constants.EXTRA_COMANDA, idComanda)
                                    intent.putExtra(Constants.EXTRA_USUARIO, uidAuth)
                                    startActivity(intent)
                                },
                                onFailure = { exception ->
                                    // Maneja el error al crear la comanda
                                    Log.e("Error al crear comanda", "Error: $exception")
                                }
                            )
                        } else {
                            // No se encontró ningún camarero con ese DocumentID
                            // Maneja este caso según tus necesidades
                            Log.e("Error idCamarero", "No se encontró ningún camarero con ese DocumentID")
                        }
                    },
                    onFailure = { exception ->
                        // Maneja el error al obtener el camarero
                        println("Error al obtener el camarero: $exception")
                    }
                )
            }

            binding.bCerrarSesion.setOnClickListener {
                firebaseUtil.cerrarSesion(this)
            }
        }else{
            Log.e("Error idUser", "El uid es inválido")
        }
    }

    private fun getUsuarioInformacion(idUsuario: String) {
        firebaseUtil.obtenerUsuarioPorId(idUsuario,
            onSuccess = { usuario ->
                if (usuario != null) {
                    // Manejar el éxito, por ejemplo, mostrar los datos del usuario en la interfaz de usuario
                    createUI(usuario)
                    user = usuario
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
        binding.tvNombreCamarero.text = buildString {
            append(usuario.nombre)
            append(" ")
            append(usuario.apellido1)
        }
    }
}