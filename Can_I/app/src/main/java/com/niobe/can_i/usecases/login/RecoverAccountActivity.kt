package com.niobe.can_i.usecases.login

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityRecoverAccountBinding

class RecoverAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecoverAccountBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRecoverAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Inicializar UI
        initUI()
    }

    private fun initUI() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.bRecuperarCuenta.setOnClickListener {
            val email = binding.etEmailAddress.text.toString().trim()
            if (email.isNotEmpty()) {
                checkEmailInFirestore(email)
            } else {
                Toast.makeText(this, "Por favor ingresa tu correo electrónico", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkEmailInFirestore(email: String) {
        db.collection("usuarios")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    sendPasswordResetEmail(email)
                } else {
                    Toast.makeText(this, "Correo electrónico no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error al obtener documentos: ", exception)
                Toast.makeText(this, "Error al verificar el correo electrónico", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendPasswordResetEmail(email: String) {
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Correo de recuperación enviado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al enviar el correo de recuperación", Toast.LENGTH_SHORT).show()
                }
            }
    }
}