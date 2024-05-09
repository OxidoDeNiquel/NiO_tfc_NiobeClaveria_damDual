package com.niobe.can_i.usecases.admin_menu.admin_articulos.sel_articulo.edit_articulo

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityEditArticuloBinding
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.usecases.admin_menu.admin_articulos.GestionArticulosActivity
import com.niobe.can_i.util.Constants

class EditArticuloActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditArticuloBinding

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

        // Inicializar la interfaz de usuario y cargar los datos del artículo
        initUI()
    }

    private fun initUI() {
        val articuloId = intent.getStringExtra("ARTICULO")

        val tipoBebida = resources.getStringArray(R.array.tipos_de_bebidas)
        val adapter = ArrayAdapter(
            this, R.layout.list_tipos_bebidas, tipoBebida
        )

        with(binding.actvTipoBebida){
            setAdapter(adapter)
        }

        if (articuloId != null) {
            // Obtener los detalles del artículo y mostrarlos en la interfaz de usuario
            getArticulo(articuloId,
                onSuccess = { articulo ->
                    if (articulo != null) {
                        createUI(articulo)
                    } else {
                        Toast.makeText(this, "No se encontró ningún artículo", Toast.LENGTH_SHORT).show()
                    }
                },
                onFailure = { errorMessage ->
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.bConfirmar.setOnClickListener {
            // Actualizar el artículo con los nuevos datos
            if (articuloId != null) {
                getArticulo(articuloId,
                    onSuccess = { articulo ->
                        if (articulo != null) {
                            val nuevoArticulo = Articulo(
                                articulo.articuloId,
                                binding.etNombreArticulo.text.toString(),
                                binding.actvTipoBebida.text.toString(),
                                binding.etPrecio.text.toString().toDouble(),
                                binding.etStock.text.toString().toInt()
                            )
                            updateArticulo(articulo.articuloId, nuevoArticulo)
                        } else {
                            Toast.makeText(
                                this,
                                "No se encontró ningún artículo",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onFailure = { errorMessage ->
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            }

        }
        binding.bCancelar.setOnClickListener {
            // Volver a la actividad de gestión de artículos
            goToGestionArticulosActivity()
        }
    }

    private fun createUI(articulo: Articulo) {
        binding.etNombreArticulo.setText(articulo.nombre)
        binding.etPrecio.setText(articulo.precio.toString())
        binding.etStock.setText(articulo.stock.toString())
    }

    private fun getArticulo(articuloId: String, onSuccess: (Articulo?) -> Unit, onFailure: (String) -> Unit) {
        // Obtener una referencia a la base de datos Firebase
        val databaseReference = FirebaseDatabase.getInstance("https://tu-proyecto.firebaseio.com")
            .getReference("articulos")

        // Realizar la consulta para obtener el artículo con el articuloId dado
        databaseReference.orderByChild("articuloId").equalTo(articuloId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // La consulta devuelve resultados
                        // En este caso, solo debería haber un artículo con el articuloId específico
                        val articuloSnapshot = dataSnapshot.children.first() // Obtener el primer resultado
                        val articulo = articuloSnapshot.getValue(Articulo::class.java)
                        onSuccess(articulo)
                    } else {
                        // No se encontró ningún artículo con el articuloId dado
                        onFailure("No existe ningún artículo con articuloId $articuloId")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Manejar errores de lectura de la base de datos
                    onFailure("Error al leer datos de Firebase: ${databaseError.message}")
                }
            })
    }

    private fun updateArticulo(articuloId: String, nuevoArticulo: Articulo) {
        val databaseReference = FirebaseDatabase.getInstance(Constants.INSTANCE)
            .getReference("articulos")
            .child(articuloId)

        databaseReference.setValue(nuevoArticulo)
            .addOnSuccessListener {
                // La actualización fue exitosa
                // Puedes mostrar un Toast u otra acción
                Toast.makeText(this, "Artículo actualizado correctamente", Toast.LENGTH_SHORT).show()
                goToGestionArticulosActivity()
            }
            .addOnFailureListener { exception ->
                // La actualización falló
                // Puedes mostrar un Toast o manejar el error de otra manera
                Toast.makeText(this, "Error al actualizar el artículo", Toast.LENGTH_SHORT).show()
            }
    }

    private fun goToGestionArticulosActivity() {
        // Iniciar la actividad de gestión de artículos
        val intent = Intent(this, GestionArticulosActivity::class.java)
        startActivity(intent)
        finish() // Cerrar la actividad actual
    }
}
