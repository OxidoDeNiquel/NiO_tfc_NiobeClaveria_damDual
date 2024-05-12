package com.niobe.can_i.usecases.admin_menu.admin_barras.sel_barra

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivitySelBarraBinding
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.model.Barra
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.admin_menu.admin_barras.GestionBarrasActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util

class SelBarraActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelBarraBinding
    private lateinit var firebaseUtil: FirebaseUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySelBarraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseUtil = FirebaseUtil()

        initUI()
    }

    private fun initUI() {
        val id: String? = intent.getStringExtra(Constants.EXTRA_ID)
        if (id != null) {
            getBarraInformation(id)
            binding.ivBack.setOnClickListener {
                finish()
            }
            binding.bCancelar.setOnClickListener {
                Util.changeActivity(this, GestionBarrasActivity::class.java)
            }
            binding.bBorrarBarra.setOnClickListener {
                deleteBarra(id)
            }
        } else {
            // Manejar el caso donde no se proporciona el ID del artículo
            Toast.makeText(this, "ID de artículo no válido", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun deleteBarra(barraId: String) {
        firebaseUtil.borrarBarra(barraId,
            onSuccess = {
                Toast.makeText(this, "Barra eliminada con éxito", Toast.LENGTH_SHORT).show()
                finish()
            },
            onFailure = { errorMessage ->
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        )
    }


    private fun getBarraInformation(idBarra: String) {
        firebaseUtil.getBarra(
            idBarra,
            onSuccess = { barra ->
                barra?.let {
                    runOnUiThread {
                        createUI(barra)
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

    private fun createUI(barra: Barra) {
        binding.tvNombreBarra.text = buildString {
            append("Barra ")
            append(barra.ubicacion)
        }
    }
}