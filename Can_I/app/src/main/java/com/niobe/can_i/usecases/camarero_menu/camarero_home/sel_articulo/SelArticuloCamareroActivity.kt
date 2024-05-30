package com.niobe.can_i.usecases.camarero_menu.camarero_home.sel_articulo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivitySelArticuloCamareroBinding
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.model.ArticulosComanda
import com.niobe.can_i.model.Comanda
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util
import java.util.UUID

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
                añadirArticuloAComanda(idComanda, idCamarero, idArticulo, cantidad)
            }
        } else {
            // Manejar el caso donde no se proporciona el ID del artículo
            Toast.makeText(this, "ID de artículo no válido", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setWeight() {
        binding.tvNumber.text = cantidad.toString()
    }

    fun añadirArticuloAComanda(idComanda: String, idCamarero: String, idArticulo: String, cantidad: Int) {
        val db = FirebaseFirestore.getInstance()

        // Obtener el camarero por ID
        firebaseUtil.obtenerCamareroPorId(idCamarero, { camarero ->
            if (camarero != null) {
                // Obtener el artículo por ID
                firebaseUtil.obtenerArticuloPorId(idArticulo, { articulo ->
                    if (articulo != null) {
                        // Generar un ID aleatorio para idArticulosComanda
                        val idArticulosComanda = UUID.randomUUID().toString()

                        // Crear una instancia de Comanda
                        val comanda = Comanda(idComanda = idComanda, idCamarero = camarero, fechaHora = Util.obtenerFechaHoraActual())

                        // Crear una instancia de ArticulosComanda
                        val articuloComanda = ArticulosComanda(
                            idArticulosComanda = idArticulosComanda,
                            idComanda = comanda,
                            idArticulo = articulo,
                            cantidad = cantidad
                        )

                        // Añadir el objeto a la colección "articulos_comanda" en Firestore
                        db.collection("articulos_comanda")
                            .document(idArticulosComanda)
                            .set(articuloComanda)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Producto añadido al pedido con éxito", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al añadir el producto al pedido", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Artículo no encontrado", Toast.LENGTH_SHORT).show()
                    }
                }, { e ->
                    Toast.makeText(this, "Error al obtener el artículo", Toast.LENGTH_SHORT).show()
                })
            } else {
                Toast.makeText(this, "Camarero no encontrado", Toast.LENGTH_SHORT).show()
            }
        }, { e ->
            Toast.makeText(this, "Error al obtener el camarero", Toast.LENGTH_SHORT).show()
        })
    }

    private fun getArticuloInformation(articuloId: String) {
        firebaseUtil.getArticulo(
            articuloId,
            onSuccess = { articulo ->
                articulo?.let {
                    runOnUiThread {
                        createUI(articulo)
                    }
                } ?: runOnUiThread {
                    // Manejar caso donde no se puede obtener el artículo
                    Toast.makeText(this, "No se encontró ningún artículo", Toast.LENGTH_SHORT).show()
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

    private fun createUI(articulo: Articulo) {
        binding.tvNombreArticulo.text = articulo.nombre
        binding.tvPrecio.text = "${articulo.precio}€"
        val imageUrl = articulo.imagenUrl

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .into(binding.ivArticulo)
        } else {
            binding.ivArticulo.setImageResource(R.drawable.ic_launcher_foreground) // Imagen de reserva
        }
    }
}
