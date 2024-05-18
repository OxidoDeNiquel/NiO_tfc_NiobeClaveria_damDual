package com.niobe.can_i.usecases.camarero_menu.camarero_home.cesta.sel_articulo_cesta

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivitySelArticuloCestaBinding
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.model.ArticulosComanda
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.util.Constants

class SelArticuloCestaActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelArticuloCestaBinding
    private lateinit var firebaseUtil: FirebaseUtil
    private var cantidad: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySelArticuloCestaBinding.inflate(layoutInflater)
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
            getArticuloComanda(idComanda, idArticulo) { articuloComanda ->
                articuloComanda?.let {
                    cantidad = it.cantidad
                    setWeight()
                } ?: run {
                    Toast.makeText(this, "No se encontró el artículo en la cesta", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            binding.ivBack.setOnClickListener {
                finish()
            }
            binding.btnSubstract.setOnClickListener {
                if (cantidad > 0) {
                    cantidad -= 1
                    setWeight()
                }
            }
            binding.btnAdd.setOnClickListener {
                cantidad += 1
                setWeight()
            }
            binding.bConfirmar.setOnClickListener {
                updateArticuloComanda(idComanda, idArticulo)
                finish()
            }
            binding.bBorrarArticulo.setOnClickListener {
                deleteArticuloComanda(idComanda, idArticulo)
                finish()
            }
        } else {
            // Manejar el caso donde no se proporciona el ID del artículo
            Toast.makeText(this, "ID de artículo no válido", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun deleteArticuloComanda(idComanda: String, idArticulo: String) {
        firebaseUtil.deleteArticuloComanda(idComanda, idArticulo) { success ->
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, "Artículo eliminado", Toast.LENGTH_SHORT).show()
                    finish() // Finaliza la actividad después de eliminar el artículo
                } else {
                    Toast.makeText(this, "Error al eliminar el artículo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateArticuloComanda(idComanda: String, idArticulo: String) {
        firebaseUtil.updateArticuloComanda(idComanda, idArticulo, cantidad) { success ->
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, "Cantidad actualizada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al actualizar la cantidad", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getArticuloComanda(idComanda: String, idArticulo: String, callback: (ArticulosComanda?) -> Unit) {
        firebaseUtil.getArticuloComandaByComandaAndArticulo(idComanda, idArticulo) { articuloComanda ->
            callback(articuloComanda)
            Log.d("FirebaseUtil", "articuloComanda: $articuloComanda")
        }
    }

    private fun setWeight() {
        binding.tvCantidad.text = cantidad.toString()
    }

    private fun getArticuloInformation(articuloId: String) {
        firebaseUtil.getArticuloById(articuloId) { articulo ->
            articulo?.let {
                runOnUiThread {
                    createUI(articulo)
                }
            } ?: runOnUiThread {
                // Manejar caso donde no se puede obtener el artículo
                Toast.makeText(this, "No se encontró ningún artículo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createUI(articulo: Articulo) {
        binding.tvNombreArticulo.text = articulo.nombre
        binding.tvPrecio.text = "${articulo.precio}€"
    }
}
