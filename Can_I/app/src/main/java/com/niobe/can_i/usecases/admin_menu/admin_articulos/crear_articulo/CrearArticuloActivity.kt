package com.niobe.can_i.usecases.admin_menu.admin_articulos.crear_articulo

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityCrearArticuloBinding
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.admin_menu.admin_articulos.GestionArticulosActivity
import java.util.UUID

class CrearArticuloActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearArticuloBinding
    private lateinit var firebaseUtil: FirebaseUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearArticuloBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseUtil = FirebaseUtil()

        // Inicializamos los componentes
        initUI()
    }

    // Función para inicializar los componentes y SetOnClickListeners
    private fun initUI() {
        // SetOnClickListeners
        binding.bCancelar.setOnClickListener {
            finish()
        }
        binding.bCrearArticulo.setOnClickListener {
            crearArticulo()
        }

        // Componentes
        val tipoBebida = resources.getStringArray(R.array.tipos_de_bebidas)
        val adapter = ArrayAdapter(
            this, R.layout.list_desplegable, tipoBebida
        )

        with(binding.actvTipoBebida) {
            setAdapter(adapter)
        }
    }

    private fun crearArticulo() {
        val nombreArticulo = binding.etNombreArticulo.text.toString()
        val tipoArticulo = binding.actvTipoBebida.text.toString()
        val precioArticulo = binding.etPrecio.text.toString().toDoubleOrNull()
        val stockArticulo = binding.etStock.text.toString().toIntOrNull()

        if (nombreArticulo.isNotEmpty() && tipoArticulo.isNotEmpty() && precioArticulo != null && stockArticulo != null) {
            val articuloId = UUID.randomUUID().toString() // Generar un ID aleatorio
            val articulo = Articulo(articuloId, nombreArticulo, tipoArticulo, precioArticulo, stockArticulo)
            firebaseUtil.guardarArticulo(articuloId, articulo) { success ->
                if (success) {
                    Toast.makeText(this, "Artículo creado exitosamente", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, GestionArticulosActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al crear el artículo", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }
}