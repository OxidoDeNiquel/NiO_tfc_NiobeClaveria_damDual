package com.niobe.can_i.usecases.admin_menu.admin_articulos.sel_articulo

import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivitySelArticuloBinding
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.provider.preferences.roomdb.CanIDatabase
import com.niobe.can_i.provider.preferences.roomdb.entities.ArticuloEntity
import com.niobe.can_i.usecases.admin_menu.admin_articulos.GestionArticulosActivity
import com.niobe.can_i.usecases.admin_menu.admin_articulos.sel_articulo.edit_articulo.EditArticuloActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelArticuloActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelArticuloBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySelArticuloBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUI()
    }

    private fun initUI(){
        val id: String? = intent.getStringExtra(Constants.EXTRA_ID)
        getArticuloInformation(id ?: "")

        binding.ivBack.setOnClickListener {
            goToGestionArticulosActivity()
        }
        binding.bEditarArticulo.setOnClickListener {
            navigateToEditArticulo(id ?: "")
            Util.changeActivity(this, EditArticuloActivity::class.java)
        }
        binding.bBorrarArticulo.setOnClickListener {
            deleteArticulo(id ?: "")
        }
    }

    private fun navigateToEditArticulo(articuloId: String) {
        val intent = Intent(this, EditArticuloActivity::class.java)
        intent.putExtra("ARTICULO", articuloId)
        startActivity(intent)
    }


    private fun goToGestionArticulosActivity() {
        // Iniciamos la actividad de destino después de que se complete la operación de guardar el artículo en Firebase
        val intent = Intent(this, GestionArticulosActivity::class.java)
        startActivity(intent)
        finish() // Cerrar la actividad actual
    }

    private fun deleteArticulo(articuloId: String) {
        // Obtener una referencia a la base de datos Firebase
        val databaseReference = FirebaseDatabase.getInstance(Constants.INSTANCE)
            .getReference("articulos")

        // Realizar una consulta para encontrar la clave del artículo con el articuloId dado
        databaseReference.orderByChild("articuloId").equalTo(articuloId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // La consulta devuelve resultados
                        // En este caso, solo debería haber un artículo con el articuloId específico
                        val articuloSnapshot = dataSnapshot.children.first() // Obtener el primer resultado
                        val key = articuloSnapshot.key // Obtener la clave del artículo
                        if (key != null) {
                            // Eliminar el artículo utilizando la clave obtenida
                            databaseReference.child(key).removeValue()
                                .addOnSuccessListener {
                                    // La eliminación fue exitosa
                                    val mensaje = "Artículo eliminado con éxito"
                                    Toast.makeText(this@SelArticuloActivity, mensaje, Toast.LENGTH_SHORT).show()
                                    // Aquí puedes realizar cualquier acción adicional después de eliminar el artículo, si es necesario
                                    goToGestionArticulosActivity()
                                }
                                .addOnFailureListener { exception ->
                                    // La eliminación falló
                                    val mensaje = "Error al eliminar artículo: ${exception.message}"
                                    Toast.makeText(this@SelArticuloActivity, mensaje, Toast.LENGTH_SHORT).show()
                                    // Aquí puedes manejar el error de eliminación, si es necesario
                                    goToGestionArticulosActivity()
                                }
                        } else {
                            // No se encontró la clave del artículo
                            val mensaje = "No se encontró la clave del artículo con articuloId $articuloId"
                            Toast.makeText(this@SelArticuloActivity, mensaje, Toast.LENGTH_SHORT).show()
                            goToGestionArticulosActivity()
                        }
                    } else {
                        // No se encontró ningún artículo con el articuloId dado
                        val mensaje = "No existe ningún artículo con articuloId $articuloId"
                        Toast.makeText(this@SelArticuloActivity, mensaje, Toast.LENGTH_SHORT).show()
                        goToGestionArticulosActivity()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Manejar errores de lectura de la base de datos
                    val mensaje = "Error al leer datos de Firebase: ${databaseError.message}"
                    Toast.makeText(this@SelArticuloActivity, mensaje, Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun getArticuloInformation(articuloId: String) {
        Log.i("ArticuloId", articuloId)

        // Obtener una referencia a la base de datos Firebase
        val databaseReference = FirebaseDatabase.getInstance("https://can-i-oxidodeniquel-2024-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("articulos")

        // Definir la consulta para obtener el artículo con el articuloId específico
        val query = databaseReference.orderByChild("articuloId").equalTo(articuloId)

        // Agregar un listener para manejar los resultados de la consulta
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // La consulta devuelve resultados
                    // En este caso, solo debería haber un artículo con el articuloId específico
                    val articuloSnapshot = dataSnapshot.children.first() // Obtener el primer resultado
                    val articulo = articuloSnapshot.getValue(Articulo::class.java)

                    if (articulo != null) {
                        // Ejecutar en el hilo principal para actualizar la UI
                        runOnUiThread {
                            createUI(articulo)
                        }
                    } else {
                        // Manejar caso donde no se puede obtener el artículo
                        Log.e("getArticuloInformation", "No se pudo obtener el artículo")
                    }
                } else {
                    // Manejar caso donde el artículo no existe
                    Log.e("getArticuloInformation", "No existe ningún artículo con articuloId $articuloId")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar errores de lectura de la base de datos
                Log.e("getArticuloInformation", "Error al leer datos de Firebase: ${databaseError.message}")
            }
        })
    }

    private fun createUI(articulo: Articulo) {
        binding.tvNombreArticulo.text = articulo.nombre
        binding.tvPrecio.text = buildString {
            append(articulo.precio.toString())
            append("€")
        }
        binding.tvStockValue.text = articulo.stock.toString()
    }
}