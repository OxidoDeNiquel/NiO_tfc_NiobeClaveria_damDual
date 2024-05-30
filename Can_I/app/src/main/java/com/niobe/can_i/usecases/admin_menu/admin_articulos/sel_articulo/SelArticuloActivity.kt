package com.niobe.can_i.usecases.admin_menu.admin_articulos.sel_articulo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivitySelArticuloBinding
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.admin_menu.admin_articulos.sel_articulo.edit_articulo.EditArticuloActivity
import com.niobe.can_i.util.Constants

class SelArticuloActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelArticuloBinding
    private lateinit var firebaseUtil: FirebaseUtil
    private var articuloId: String? = null

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

        firebaseUtil = FirebaseUtil()

        articuloId = intent.getStringExtra(Constants.EXTRA_ID)

        initUI()
    }

    override fun onResume() {
        super.onResume()
        articuloId?.let { getArticuloInformation(it) }
    }

    private fun initUI() {
        if (articuloId != null) {
            binding.ivBack.setOnClickListener {
                finish()
            }
            binding.bEditarArticulo.setOnClickListener {
                navigateToEditArticulo(articuloId!!)
            }
            binding.bBorrarArticulo.setOnClickListener {
                deleteArticulo(articuloId!!)
            }
        } else {
            // Manejar el caso donde no se proporciona el ID del artículo
            Toast.makeText(this, "ID de artículo no válido", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun navigateToEditArticulo(articuloId: String) {
        val intent = Intent(this, EditArticuloActivity::class.java)
        intent.putExtra("ARTICULO", articuloId)
        startActivity(intent)
    }

    private fun deleteArticulo(articuloId: String) {
        firebaseUtil.eliminarArticulo(articuloId) { success ->
            if (success) {
                Toast.makeText(this, "Artículo eliminado con éxito", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al eliminar el artículo", Toast.LENGTH_SHORT).show()
            }
        }
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
        binding.tvStockValue.text = articulo.stock.toString()
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
