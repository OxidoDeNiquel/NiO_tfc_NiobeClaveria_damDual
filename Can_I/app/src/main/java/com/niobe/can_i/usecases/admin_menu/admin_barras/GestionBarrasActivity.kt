package com.niobe.can_i.usecases.admin_menu.admin_barras

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityGestionBarrasBinding
import com.niobe.can_i.model.Barra
import com.niobe.can_i.usecases.admin_menu.AdminMenuActivity
import com.niobe.can_i.usecases.admin_menu.admin_articulos.GestionArticulosAdapter
import com.niobe.can_i.usecases.admin_menu.admin_articulos.sel_articulo.SelArticuloActivity
import com.niobe.can_i.usecases.admin_menu.admin_barras.sel_barra.SelBarraActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util
import java.util.*

class GestionBarrasActivity : AppCompatActivity(){

    private lateinit var binding: ActivityGestionBarrasBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val barrasCollection = firestore.collection("barras")
    private lateinit var barrasList: List<Barra>

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
        onResume()

        // Configurar el clic en el botón de crear barra
        binding.bCrearBarra.setOnClickListener {
            crearBarra()
        }

        // Configurar el clic en el botón de regresar al menú
        binding.tvInicio.setOnClickListener {
            Util.changeActivity(this, AdminMenuActivity::class.java)
        }
    }
    private fun crearBarra() {
        val idBarra = UUID.randomUUID().toString()
        generarUbicacionUnica { ubicacion ->
            val nuevaBarra = Barra(idBarra, ubicacion)
            guardarBarraEnFirestore(nuevaBarra)
            onResume()
        }
    }

    private fun generarUbicacionUnica(onSuccess: (Int) -> Unit) {
        barrasCollection.get()
            .addOnSuccessListener { documents ->
                val ubicacionesExistentes = mutableSetOf<Int>()
                for (document in documents) {
                    val ubicacion = document.getLong("ubicacion")?.toInt()
                    ubicacion?.let {
                        ubicacionesExistentes.add(it)
                    }
                }

                // Obtener la ubicación más alta utilizada
                val maxUbicacion = ubicacionesExistentes.maxOrNull() ?: 0

                // Incrementar la ubicación más alta en 1 para obtener la nueva ubicación única
                val nuevaUbicacion = maxUbicacion + 1

                onSuccess(nuevaUbicacion)
            }
            .addOnFailureListener { e ->
                Log.e("Error", "Error al obtener las ubicaciones de las barras existentes: $e")
            }
    }


    private fun guardarBarraEnFirestore(barra: Barra) {
        barrasCollection
            .document(barra.idBarra)
            .set(barra)
            .addOnSuccessListener {
                val mensaje = "Barra almacenada correctamente en Firestore con ID: ${barra.idBarra}"
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                val mensaje = "Error al almacenar barra en Firestore: $e"
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        actualizarRecyclerViews()
    }

    private fun actualizarRecyclerViews() {
        leerBarras(binding.rvBarras)
    }

    private fun leerBarras(recyclerView: RecyclerView) {
        barrasCollection.get()
            .addOnSuccessListener { documents ->
                val listaBarras = mutableListOf<Barra>()
                for (document in documents) {
                    val idBarra = document.getString("idBarra") ?: ""
                    val ubicacion = document.getLong("ubicacion")?.toInt() ?: 0
                    val barra = Barra(idBarra, ubicacion)
                    listaBarras.add(barra)
                }

                // Ordenar la lista de barras por ubicación de forma creciente
                val barrasOrdenadas = listaBarras.sortedBy { it.ubicacion }

                barrasList = barrasOrdenadas
                val adapter = GestionBarrasAdapter { articuloId -> navigateToDetail(articuloId) }
                Util.setupRecyclerViewVertical(this, recyclerView, adapter)
                adapter.updateList(barrasOrdenadas)
            }
            .addOnFailureListener { e ->
                Util.showToast(this, "Error al obtener las barras desde Firestore: $e")
            }
    }

    private fun navigateToDetail(id: String) {
        val intent = Intent(this, SelBarraActivity::class.java)
        intent.putExtra(Constants.EXTRA_ID, id)
        startActivity(intent)
    }
}
