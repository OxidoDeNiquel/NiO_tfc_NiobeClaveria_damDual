package com.niobe.can_i.usecases.camarero_menu.camarero_home.sel_articulo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivitySelArticuloCamareroBinding
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.model.ArticulosComanda
import com.niobe.can_i.model.Comanda
import com.niobe.can_i.model.Camarero
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.util.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SelArticuloCamareroActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelArticuloCamareroBinding
    private lateinit var firebaseUtil: FirebaseUtil
    private var cantidad: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySelArticuloCamareroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa firebaseUtil
        firebaseUtil = FirebaseUtil()

        initUI()

    }

    private fun initUI() {
        val idArticulo = intent.getStringExtra(Constants.EXTRA_ID)
        val idComanda = intent.getStringExtra(Constants.EXTRA_COMANDA)
        val idCamarero = intent.getStringExtra(Constants.EXTRA_USUARIO)
        if (idArticulo != null && idComanda != null && idCamarero != null) {
            getArticuloInformation(idArticulo)
            binding.ivBack.setOnClickListener {
                finish()
            }
            binding.btnSubstract.setOnClickListener {
                cantidad -= 1
                setWeight()
            }
            binding.btnAdd.setOnClickListener {
                cantidad += 1
                setWeight()
            }
            binding.bAnadirCesta.setOnClickListener {
                // Lanzar una coroutine para obtener el camarero y la comanda
                lifecycleScope.launch {
                    val camarero = obtenerCamareroPorId(idCamarero)
                    if (camarero != null) {
                        /*val comanda = obtenerComandaPorId(idComanda, camarero)
                        obtenerArticuloYCrearArticuloComanda(idArticulo, comanda)*/
                    } else {
                        // Manejar el caso donde no se pudo obtener el camarero
                        Toast.makeText(this@SelArticuloCamareroActivity, "No se pudo obtener el camarero", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            // Manejar el caso donde no se proporciona el ID del artículo
            Toast.makeText(this, "ID de artículo no válido", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setWeight(){
        binding.tvNumber.text = cantidad.toString()
    }

    private fun obtenerArticuloYCrearArticuloComanda(articuloId: String, tuComanda: Comanda) {
        firebaseUtil.getArticulo(articuloId,
            onSuccess = { articulo ->
                // Verificar si el artículo se obtuvo correctamente
                if (articulo != null) {
                    // Llamar a la función para crear el artículo de comanda utilizando el artículo obtenido
                    crearArticuloComanda(tuComanda, articulo)
                } else {
                    // Manejar el caso donde no se pudo obtener el artículo
                    println("No se pudo obtener el artículo correspondiente al ID: $articuloId")
                }
            },
            onFailure = { errorMessage ->
                // Manejar el error al obtener el artículo
                println(errorMessage)
            }
        )
    }

    private fun crearArticuloComanda(tuComanda: Comanda, articulo: Articulo) {
        // Crear un nuevo artículo de comanda con el artículo obtenido y tu Comanda
        val nuevoArticuloComanda = ArticulosComanda(
            idArticulosComanda = "", // Este será generado automáticamente en la función crearArticuloComanda
            idComanda = tuComanda,
            idArticulo = articulo,
            cantidad = 1 // Cantidad del artículo en la comanda
        )

        // Llamar a la función para crear el artículo de comanda
        firebaseUtil.crearArticuloComanda(nuevoArticuloComanda,
            onSuccess = {
                // Manejar el éxito, por ejemplo, mostrar un mensaje de éxito
                println("Artículo de comanda creado exitosamente")
            },
            onFailure = { exception ->
                // Manejar el error al crear el artículo de comanda
                println("Error al crear el artículo de comanda: $exception")
            }
        )
    }

    private fun getArticuloInformation(articuloId: String) {
        firebaseUtil.getArticulo(
            articuloId,
            onSuccess = { articulo ->
                runOnUiThread {
                    // Verificar si el artículo se obtuvo correctamente
                    if (articulo != null) {
                        createUI(articulo)
                    } else {
                        // Manejar caso donde no se puede obtener el artículo
                        Toast.makeText(this, "No se encontró ningún artículo", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            },
            onFailure = { errorMessage ->
                runOnUiThread {
                    // Manejar el caso de error
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
    private suspend fun obtenerCamareroPorId(documentId: String): Camarero? {
        return suspendCoroutine { continuation ->
            firebaseUtil.obtenerCamareroPorId(
                documentId,
                onSuccess = { obtainedCamarero ->
                    continuation.resume(obtainedCamarero)
                },
                onFailure = { exception ->
                    // Manejar el error aquí si es necesario
                    Log.e("Error obtener Camarero", "Error al obtener el camarero: $exception")
                    continuation.resume(null) // Si ocurre un error, resumir con null
                }
            )
        }
    }

    /*private suspend fun obtenerComandaPorId(documentId: String, camarero: Camarero): Comanda? {
        return suspendCoroutine { continuation ->
            firebaseUtil.obtenerComandaPorId(
                documentId,
                camarero,
                onSuccess = { obtainedComanda ->
                    continuation.resume(obtainedComanda)
                },
                onFailure = { exception ->
                    // Manejar el error aquí si es necesario
                    Log.e("Error obtener Comanda", "Error al obtener la comanda: $exception")
                    continuation.resume(null)
                }
            )
        }
    }*/

    private fun createUI(articulo: Articulo) {
        binding.tvNombreArticulo.text = articulo.nombre
        binding.tvPrecio.text = "${articulo.precio}€"
    }
}
