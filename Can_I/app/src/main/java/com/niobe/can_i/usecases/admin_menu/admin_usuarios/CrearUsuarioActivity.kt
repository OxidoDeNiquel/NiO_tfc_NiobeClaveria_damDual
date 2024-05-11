package com.niobe.can_i.usecases.admin_menu.admin_usuarios

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityCrearUsuarioBinding

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

    private fun initUI(){
        // Componentes
        val tipoBebida = resources.getStringArray(R.array.tipos_de_usuarios)
        val adapter = ArrayAdapter(
            this, R.layout.list_desplegable, tipoBebida
        )

        with(binding.actvTipoUsuario) {
            setAdapter(adapter)
        }

        val etEmail = binding.etEmail
        val etContrasena = binding.etContrasena
        val etRol = binding.actvTipoUsuario

        binding.bCrearUsuario.setOnClickListener {
            val email = etEmail.text.toString()
            val contrasena = etContrasena.text.toString()
            val rol = etRol.text.toString()

            if (email.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registrarUsuario(email, contrasena, rol)
        }
    }

    private fun registrarUsuario(email: String, contrasena: String, rol: String) {
        auth.createUserWithEmailAndPassword(email, contrasena)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registro exitoso
                    val user = auth.currentUser
                    user?.let {
                        val uid = it.uid
                        guardarUsuarioEnFirestore(email, uid, rol)
                    }
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                } else {
                    // Registro fallido
                    Log.e("ERROR CREAR USUARIO", "Error al registrar el usuario", task.exception)
                    Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun guardarUsuarioEnFirestore(email: String, uid: String, rol: String) {
        val db = FirebaseFirestore.getInstance()
        val user = hashMapOf(
            "email" to email,
            "rol" to rol
        )

        db.collection("usuarios")
            .document(uid)
            .set(user)
            .addOnSuccessListener {
                Log.d("Guardar usuario", "Usuario agregado a Firestore con UID: $uid")
            }
            .addOnFailureListener { e ->
                Log.w("Guardar usuario", "Error al agregar usuario a Firestore", e)
            }
    }
}
