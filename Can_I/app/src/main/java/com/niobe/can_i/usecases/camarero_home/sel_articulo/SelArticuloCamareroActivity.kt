package com.niobe.can_i.usecases.camarero_home.sel_articulo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivitySelArticuloBinding
import com.niobe.can_i.databinding.ActivitySelArticuloCamareroBinding
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.util.Constants

class SelArticuloCamareroActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelArticuloCamareroBinding
    private lateinit var firebaseUtil: FirebaseUtil
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
        val id: String? = intent.getStringExtra(Constants.EXTRA_ID)
        if (id != null) {
            getArticuloInformation(id)
            binding.ivBack.setOnClickListener {
                finish()
            }
        } else {
            // Manejar el caso donde no se proporciona el ID del artículo
            Toast.makeText(this, "ID de artículo no válido", Toast.LENGTH_SHORT).show()
            finish()
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
    }
}