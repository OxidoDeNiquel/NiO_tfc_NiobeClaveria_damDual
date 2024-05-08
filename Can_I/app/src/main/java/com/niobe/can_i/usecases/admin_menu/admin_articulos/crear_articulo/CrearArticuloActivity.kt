package com.niobe.can_i.usecases.admin_menu.admin_articulos.crear_articulo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityCrearArticuloBinding
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.usecases.admin_menu.admin_articulos.GestionArticulosActivity
import java.util.UUID

class CrearArticuloActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearArticuloBinding
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
        //Inicializamos los componentes
        initUI()
    }

    //Función para inicializar los componentes y SetOnClickListeners
    private fun initUI(){
        //SetOnClickListeners
        binding.bCancelar.setOnClickListener {
            goToGestionArticulosActivity()
        }
        binding.bCrearArticulo.setOnClickListener {
            crearArticulo()
            goToGestionArticulosActivity()
        }

        //Componentes
        val tipoBebida = resources.getStringArray(R.array.tipos_de_bebidas)
        val adapter = ArrayAdapter(
            this, R.layout.list_tipos_bebidas, tipoBebida
        )

        with(binding.actvTipoBebida){
            setAdapter(adapter)
        }
    }

    private fun goToGestionArticulosActivity() {
        // Iniciamos la actividad de destino después de que se complete la operación de guardar el artículo en Firebase
        val intent = Intent(this, GestionArticulosActivity::class.java)
        startActivity(intent)
        finish() // Cerrar la actividad actual
    }

    private fun crearArticulo(){
        // Obtener una instancia de la base de datos Firebase
        val databaseReference = FirebaseDatabase.getInstance("https://can-i-oxidodeniquel-2024-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("articulos")

        // Obtener los datos del formulario
        val articuloId = UUID.randomUUID().toString()
        val nombreArticulo = binding.etNombreArticulo.text.toString()
        val tipoArticulo = binding.actvTipoBebida.text.toString() // Usar autoCompleteTextView aquí
        val precioArticulo = binding.etPrecio.text.toString().toDouble()
        val stockArticulo = binding.etStock.text.toString().toInt()

        // Crear un objeto Artículo
        val articulo = Articulo(articuloId, nombreArticulo, tipoArticulo, precioArticulo, stockArticulo)

        // Generar un ID único para el artículo
        val key = databaseReference.push().key

        // Guardar el objeto Artículo en la base de datos
        if (key != null) {
            databaseReference.child(key).setValue(articulo)
                .addOnSuccessListener {
                    // Handle success
                    Toast.makeText(applicationContext, "Artículo creado exitosamente", Toast.LENGTH_SHORT).show()
                    Log.i("Ole ole se ha ejecutado", "Se ha mandado pal firebase")

                    // Set the result as RESULT_OK before finishing the activity
                    val resultIntent = Intent()
                    resultIntent.putExtra("key", key) // Pasar la clave del artículo de vuelta a la actividad principal
                    setResult(RESULT_OK, resultIntent)
                }
                .addOnFailureListener { e ->
                    // Handle error
                    Toast.makeText(applicationContext, "Error al crear el artículo: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.i("Fatal", "Todo mal")
                }
        } else {
            // Handle the case where articuloId is null
            Toast.makeText(applicationContext, "Error al generar el ID del artículo", Toast.LENGTH_SHORT).show()
        }
    }
}