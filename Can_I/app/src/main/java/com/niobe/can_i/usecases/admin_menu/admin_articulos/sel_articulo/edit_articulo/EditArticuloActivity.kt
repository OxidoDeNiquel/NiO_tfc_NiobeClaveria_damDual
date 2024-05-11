package com.niobe.can_i.usecases.admin_menu.admin_articulos.sel_articulo.edit_articulo

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityEditArticuloBinding
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.admin_menu.admin_articulos.GestionArticulosActivity

class EditArticuloActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditArticuloBinding
    private val firebaseUtil = FirebaseUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditArticuloBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUI()
    }

    private fun initUI() {
        val articuloId = intent.getStringExtra("ARTICULO")
        val tipoBebida = resources.getStringArray(R.array.tipos_de_bebidas)
        val adapter = ArrayAdapter(
            this, R.layout.list_desplegable, tipoBebida
        )

        with(binding.actvTipoBebida) {
            setAdapter(adapter)
        }

        if (articuloId != null) {
            firebaseUtil.getArticulo(articuloId,
                onSuccess = { articulo ->
                    articulo?.let {
                        createUI(articulo)
                    } ?: run {
                        Toast.makeText(this, "No se encontró ningún artículo", Toast.LENGTH_SHORT).show()
                    }
                },
                onFailure = { errorMessage ->
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(this, "Error: No se proporcionó un ID de artículo", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.bConfirmar.setOnClickListener {
            if (articuloId != null) {
                val nuevoArticulo = obtenerNuevoArticulo()
                val nuevosDatos = mapOf(
                    "nombre" to nuevoArticulo.nombre,
                    "tipo" to nuevoArticulo.tipo,
                    "precio" to nuevoArticulo.precio,
                    "stock" to nuevoArticulo.stock
                )
                firebaseUtil.actualizarArticulo(articuloId, nuevosDatos,
                    onSuccess = {
                        Toast.makeText(this, "Artículo actualizado correctamente", Toast.LENGTH_SHORT).show()
                        goToGestionArticulosActivity()
                    },
                    onFailure = {
                        Toast.makeText(this, "Error al actualizar el artículo", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }



        binding.bCancelar.setOnClickListener {
            goToGestionArticulosActivity()
        }
    }

    private fun createUI(articulo: Articulo) {
        binding.etNombreArticulo.setText(articulo.nombre)
        binding.actvTipoBebida.setText(articulo.tipo)
        binding.etPrecio.setText(articulo.precio.toString())
        binding.etStock.setText(articulo.stock.toString())
    }

    private fun obtenerNuevoArticulo(): Articulo {
        val nombreArticulo = binding.etNombreArticulo.text.toString()
        val tipoArticulo = binding.actvTipoBebida.text.toString()
        val precioArticulo = binding.etPrecio.text.toString().toDoubleOrNull() ?: 0.0
        val stockArticulo = binding.etStock.text.toString().toIntOrNull() ?: 0

        return Articulo("", nombreArticulo, tipoArticulo, precioArticulo, stockArticulo)
    }

    private fun goToGestionArticulosActivity() {
        val intent = Intent(this, GestionArticulosActivity::class.java)
        startActivity(intent)
        finish()
    }
}
