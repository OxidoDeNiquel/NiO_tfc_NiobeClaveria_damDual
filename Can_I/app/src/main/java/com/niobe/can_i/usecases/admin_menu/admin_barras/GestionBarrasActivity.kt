package com.niobe.can_i.usecases.admin_menu.admin_barras

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityGestionBarrasBinding
import java.util.*

class GestionBarrasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGestionBarrasBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val barrasCollection = firestore.collection("barras")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGestionBarrasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUI()
    }

    private fun initUI() {
        binding.bCrearBarra.setOnClickListener {
            crearBarra()
        }
    }

    private fun crearBarra() {
        val idBarra = UUID.randomUUID().toString() // Generar un ID único para la barra
        val ubicacion = generarUbicacionUnica()

        val nuevaBarra = hashMapOf(
            "idBarra" to idBarra,
            "ubicacion" to ubicacion
        )

        barrasCollection.document(idBarra)
            .set(nuevaBarra)
            .addOnSuccessListener {
                println("Barra creada exitosamente con ID: $idBarra y ubicación: $ubicacion")
            }
            .addOnFailureListener { e ->
                println("Error al crear la barra: $e")
            }
    }

    private fun generarUbicacionUnica(): Int {
        val ubicacionesExistentes = mutableSetOf<Int>()
        barrasCollection.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val ubicacion = document.get("ubicacion") as Long
                    ubicacionesExistentes.add(ubicacion.toInt())
                }
            }
            .addOnFailureListener { e ->
                println("Error al obtener las ubicaciones de las barras existentes: $e")
            }

        // Generar una ubicación aleatoria única
        var ubicacionAleatoria: Int
        do {
            ubicacionAleatoria = Random().nextInt(100) + 1
        } while (ubicacionesExistentes.contains(ubicacionAleatoria))

        return ubicacionAleatoria
    }
}
