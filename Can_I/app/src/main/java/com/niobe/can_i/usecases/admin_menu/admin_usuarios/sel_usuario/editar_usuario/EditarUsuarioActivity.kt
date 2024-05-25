package com.niobe.can_i.usecases.admin_menu.admin_usuarios.sel_usuario.editar_usuario

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityEditarUsuarioBinding
import com.niobe.can_i.model.Usuario
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.admin_menu.admin_usuarios.GestionUsuariosActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util

class EditarUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarUsuarioBinding
    private lateinit var firebaseUtil: FirebaseUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditarUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseUtil = FirebaseUtil()

        initUI()
    }

    private fun initUI(){
        val id: String? = intent.getStringExtra(Constants.EXTRA_ID)
        // Componentes
        val tipoUsuarios = resources.getStringArray(R.array.tipos_de_usuarios)
        val adapter = ArrayAdapter(
            this, R.layout.list_desplegable, tipoUsuarios
        )

        with(binding.actvTipoUsuario) {
            setAdapter(adapter)
        }

        if(id != null){
            getUsuarioInformacion(id)

            binding.bActualizarUsuario.setOnClickListener {
                firebaseUtil.obtenerUsuarioPorId(id,
                    onSuccess = { usuario ->
                        if (usuario != null) {
                            // Manejar el éxito, por ejemplo, mostrar los datos del usuario en la interfaz de usuario
                            val nombre = binding.etNombre.text.toString()
                            val apell1 = binding.etApellido1.text.toString()
                            val apell2 = binding.etApellido2.text.toString()
                            val dni = binding.etDni.text.toString()
                            editarUsuario(id, nombre, apell1, apell2, dni, usuario.rol)
                            Util.showToast(this, "El usuario ha sido editado con éxito")
                            finish()
                        } else {
                            // El usuario no existe
                            Util.showToast(this, "El usuario no existe")
                            finish()
                        }
                    },
                    onFailure = { exception ->
                        // Manejar el error, por ejemplo, mostrar un mensaje de error al usuario
                        Util.showToast(this, "Error al obtener usuario: $exception")
                        finish()
                    }
                )
            }

            binding.bCancelar.setOnClickListener {
                Util.changeActivity(this, GestionUsuariosActivity::class.java)
            }
        }else{
            Log.e("Error userId", "Error al obtener el idUser")
        }
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

    fun editarUsuario(idUsuario: String, nombre: String, apellido1:String, apellido2:String, dni: String, rol: String): Task<Void> {
        val db = FirebaseFirestore.getInstance()
        val usuariosCollection = db.collection("usuarios")
        val usuarioRef = usuariosCollection.document(idUsuario)

        // Crea un mapa mutable con los campos que deseas actualizar en usuarios
        val datosUsuarioActualizados: MutableMap<String, Any?> = hashMapOf(
            "nombre" to nombre,
            "apellido1" to apellido1,
            "apellido2" to apellido2,
            "dni" to dni
        )

        // Actualiza los campos en la colección de usuarios
        val actualizarUsuarioTask = usuarioRef.update(datosUsuarioActualizados)

        // Actualiza la información del usuario en la colección de camareros
        val actualizarCamarerosTask = actualizarDocumentosCamareros(idUsuario, datosUsuarioActualizados)

        // Verifica el rol y actualiza la información en la colección correspondiente
        if (rol == Constants.TIPO_USUARIO_CAMARERO) {
            return Tasks.whenAllSuccess<Void>(actualizarUsuarioTask, actualizarCamarerosTask).continueWith { null }
        } else if (rol == Constants.TIPO_USUARIO_ADMINISTRADOR) {
            val actualizarAdministradorTask = actualizarDocumentosAdministradores(idUsuario, datosUsuarioActualizados)
            return Tasks.whenAllSuccess<Void>(actualizarUsuarioTask, actualizarAdministradorTask).continueWith { null }
        }

        // Si el rol no es ni camarero ni administrador, solo actualiza la información de usuario
        return actualizarUsuarioTask
    }

    private fun actualizarDocumentosCamareros(idUsuario: String, datosActualizados: Map<String, Any?>): Task<Void> {
        val db = FirebaseFirestore.getInstance()
        val camarerosCollection = db.collection("camareros")

        // Consulta todos los documentos en la colección de camareros donde idUsuario es igual al ID del usuario que estamos editando
        return camarerosCollection.whereEqualTo("idUsuario.idUsuario", idUsuario).get().continueWithTask { snapshotTask ->
            val updateTasks = mutableListOf<Task<Void>>()

            // Por cada documento encontrado, actualiza la información de usuario en el documento de camarero y en la subcolección idUsuario
            for (document in snapshotTask.result!!) {
                // Actualiza la información de usuario en el documento de camarero
                val datosUsuarioActualizados: MutableMap<String, Any?> = hashMapOf(
                    "idEmpleado.usuario.nombre" to datosActualizados["nombre"],
                    "idEmpleado.usuario.apellido1" to datosActualizados["apellido1"],
                    "idEmpleado.usuario.apellido2" to datosActualizados["apellido2"],
                    "idEmpleado.usuario.dni" to datosActualizados["dni"]
                )
                updateTasks.add(document.reference.update("idUsuario", datosActualizados))
                updateTasks.add(document.reference.update("idEmpleado.usuario", datosActualizados))

                // Consulta todos los documentos dentro de la subcolección idUsuario y actualiza la información del usuario en cada uno
                val idUsuarioSubcollection = document.reference.collection("idUsuario")
                updateTasks.add(idUsuarioSubcollection.get().continueWithTask { idUsuarioSnapshotTask ->
                    val updateSubcollectionTasks = mutableListOf<Task<Void>>()
                    for (idUsuarioDocument in idUsuarioSnapshotTask.result!!) {
                        updateSubcollectionTasks.add(idUsuarioDocument.reference.update(datosActualizados))
                    }
                    Tasks.whenAll(updateSubcollectionTasks)
                })
            }

            // Combinar todas las tareas de actualización en una sola tarea
            Tasks.whenAll(updateTasks)
        }
    }

    private fun actualizarDocumentosAdministradores(idUsuario: String, datosActualizados: Map<String, Any?>): Task<Void> {
        val db = FirebaseFirestore.getInstance()
        val administradoresCollection = db.collection("administradores")

        // Consulta todos los documentos en la colección de administradores donde idUsuario es igual al ID del usuario que estamos editando
        return administradoresCollection.whereEqualTo("idUsuario", idUsuario).get().continueWithTask { snapshotTask ->
            val updateTasks = mutableListOf<Task<Void>>()

            // Por cada documento encontrado, actualiza la información de usuario
            for (document in snapshotTask.result!!) {
                updateTasks.add(document.reference.update(datosActualizados))
            }

            // Combinar todas las tareas de actualización en una sola tarea
            Tasks.whenAll(updateTasks)
        }
    }


    private fun createUI(usuario: Usuario) {
        binding.etEmail.setText(usuario.email)
        binding.etNombre.setText(usuario.nombre)
        binding.etApellido1.setText(usuario.apellido1)
        binding.etApellido2.setText(usuario.apellido2)
        binding.etDni.setText(usuario.dni)
        binding.actvTipoUsuario.setText(usuario.rol)
    }
}