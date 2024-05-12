package com.niobe.can_i.usecases.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityLogInBinding
import com.niobe.can_i.usecases.admin_menu.AdminMenuActivity
import com.niobe.can_i.usecases.camarero_home.CamareroHomeActivity
import com.niobe.can_i.util.Constants

class LogInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityLogInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        initUI()
    }

    private fun initUI(){
        val etEmail = binding.etEmailAddress
        val etPassword = binding.etPassword
        val bLogIn = binding.bLogIn

        bLogIn.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                iniciarSesion(email, password)
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun iniciarSesion(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso
                    obtenerRolUsuario(auth.currentUser?.uid)
                } else {
                    // Error en el inicio de sesión
                    Log.e("LogInActivity", "Error al iniciar sesión", task.exception)
                    Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun obtenerRolUsuario(uid: String?) {
        uid?.let {
            db.collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val rol = document.getString("rol")
                        rol?.let {
                            if (it == Constants.TIPO_USUARIO_ADMINISTRADOR) {
                                // Usuario con rol de Administrador
                                startActivity(Intent(this, AdminMenuActivity::class.java))
                            } else if (it == Constants.TIPO_USUARIO_CAMARERO){
                                // Usuario con otro rol (por ejemplo, Camarero)
                                startActivity(Intent(this, CamareroHomeActivity::class.java))
                            }
                        }
                    } else {
                        Toast.makeText(this, "El usuario no tiene rol asignado", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("LogInActivity", "Error al obtener rol de usuario", e)
                    Toast.makeText(this, "Error al obtener rol de usuario", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
