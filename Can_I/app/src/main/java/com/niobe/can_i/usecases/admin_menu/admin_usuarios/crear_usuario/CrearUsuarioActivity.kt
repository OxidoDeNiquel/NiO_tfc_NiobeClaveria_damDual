package com.niobe.can_i.usecases.admin_menu.admin_usuarios.crear_usuario

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityCrearUsuarioBinding
import com.niobe.can_i.model.Administrador
import com.niobe.can_i.model.Barra
import com.niobe.can_i.model.Camarero
import com.niobe.can_i.model.Empleado
import com.niobe.can_i.model.Usuario
import com.niobe.can_i.usecases.admin_menu.AdminMenuActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util

class CrearUsuarioActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityCrearUsuarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        initUI()
    }

    private fun initUI() {
        // Componentes
        val tipoUsuarios = resources.getStringArray(R.array.tipos_de_usuarios)
        val adapter = ArrayAdapter(
            this, R.layout.list_desplegable, tipoUsuarios
        )

        with(binding.actvTipoUsuario) {
            setAdapter(adapter)
        }

        val etEmail = binding.etEmail
        val etContrasena = binding.etContrasena
        val etRol = binding.actvTipoUsuario
        val etNombre = binding.etNombre
        val etApellido1 = binding.etApellido1
        val etApellido2 = binding.etApellido2
        val etDni = binding.etDni

        binding.bCancelar.setOnClickListener {
            Util.changeActivity(this, AdminMenuActivity::class.java)
        }

        binding.bCrearUsuario.setOnClickListener {
            val email = etEmail.text.toString()
            val contrasena = etContrasena.text.toString()
            val rol = etRol.text.toString()
            val nombre = etNombre.text.toString()
            val apellido1 = etApellido1.text.toString()
            val apellido2 = etApellido2.text.toString()
            val dni = etDni.text.toString()

            if (email.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contrasena.length >= 6) {
                registrarUsuario(email, contrasena, rol, nombre, apellido1, apellido2, dni)
            } else {
                val toast = Toast.makeText(this, "La contraseña debe tener mínimo 6 caracteres", Toast.LENGTH_SHORT)
                val view = toast.view
                if (view != null) {
                    view.setBackgroundColor(Color.RED)
                    val textView = view.findViewById<TextView>(android.R.id.message)
                    textView.setTextColor(Color.WHITE)
                }
                toast.show()
            }
        }
    }

    private fun registrarUsuario(email: String, contrasena: String, rol: String, nombre: String, apellido1:String, apellido2:String, dni: String) {
        auth.createUserWithEmailAndPassword(email, contrasena)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val uid = it.uid
                        val usuario = Usuario(uid, email, rol, nombre, apellido1, apellido2, dni)
                        when (rol) {
                            Constants.TIPO_USUARIO_ADMINISTRADOR -> {
                                guardarUsuario(usuario, usuario.rol)
                            }
                            Constants.TIPO_USUARIO_CAMARERO -> {
                                guardarUsuario(usuario, usuario.rol)
                            }
                            else -> {
                                Log.e("Tipo de usuario no válido", "El tipo de usuario '$rol' no es válido")
                            }
                        }
                    }
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("ERROR CREAR USUARIO", "Error al registrar el usuario", task.exception)
                    Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun guardarUsuario(usuario: Usuario, tipoUsuario: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("usuarios")
            .document(usuario.idUsuario)
            .set(usuario)
            .addOnSuccessListener {
                Log.d("Guardar Usuario", "Usuario almacenado correctamente en Firestore")
            }
            .addOnFailureListener { e ->
                Log.e("Guardar Usuario", "Error al almacenar usuario en Firestore", e)
            }

        if (tipoUsuario == Constants.TIPO_USUARIO_ADMINISTRADOR || tipoUsuario == Constants.TIPO_USUARIO_CAMARERO) {
            guardarUsuarioSegunTipo(usuario, tipoUsuario)
        }
    }

    private fun guardarUsuarioSegunTipo(usuario: Usuario, tipoUsuario: String) {
        when (tipoUsuario) {
            Constants.TIPO_USUARIO_ADMINISTRADOR -> {
                val administrador = Administrador(usuario.idUsuario, usuario)
                guardarAdministradorEnFirestore(administrador)
            }
            Constants.TIPO_USUARIO_CAMARERO -> {
                asignarBarraYCamarero(usuario)
            }
            else -> {
                Log.e("Tipo de usuario no válido", "El tipo de usuario '$tipoUsuario' no es válido")
            }
        }
    }

    private fun asignarBarraYCamarero(usuario: Usuario) {
        val db = FirebaseFirestore.getInstance()
        obtenerBarrasDisponibles(
            db,
            onSuccess = { barrasDisponibles ->
                val barraSeleccionada = seleccionarBarraAleatoria(barrasDisponibles)
                if (barraSeleccionada != null) {
                    val camarero = Camarero(usuario.idUsuario, Empleado(usuario.idUsuario, usuario), usuario, barraSeleccionada)
                    guardarCamareroEnFirestore(camarero)
                } else {
                    Log.e("Error", "No hay barras disponibles para asignar al camarero")
                }
            },
            onFailure = { e ->
                Log.e("Error", "Error al obtener las barras disponibles", e)
            }
        )
    }

    private fun obtenerBarrasDisponibles(db: FirebaseFirestore, onSuccess: (List<Barra>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("barras")
            .get()
            .addOnSuccessListener { documents ->
                val barrasDisponibles = mutableListOf<Barra>()
                for (document in documents) {
                    val barra = document.toObject(Barra::class.java)
                    barrasDisponibles.add(barra)
                }
                onSuccess(barrasDisponibles)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    private fun seleccionarBarraAleatoria(barrasDisponibles: List<Barra>): Barra? {
        return if (barrasDisponibles.isNotEmpty()) {
            barrasDisponibles.random()
        } else {
            null
        }
    }

    private fun guardarCamareroEnFirestore(camarero: Camarero) {
        val db = FirebaseFirestore.getInstance()
        db.collection("camareros")
            .document(camarero.idCamarero)
            .set(camarero)
            .addOnSuccessListener {
                Log.d("Guardar Camarero", "Camarero almacenado correctamente en Firestore")
            }
            .addOnFailureListener { e ->
                Log.e("Guardar Camarero", "Error al almacenar camarero en Firestore", e)
            }
    }

    private fun guardarAdministradorEnFirestore(administrador: Administrador) {
        val db = FirebaseFirestore.getInstance()
        db.collection("administradores")
            .document(administrador.idAdministrador)
            .set(administrador)
            .addOnSuccessListener {
                Log.d("Guardar Administrador", "Administrador almacenado correctamente en Firestore")
            }
            .addOnFailureListener { e ->
                Log.e("Guardar Administrador", "Error al almacenar administrador en Firestore", e)
            }
    }
}
