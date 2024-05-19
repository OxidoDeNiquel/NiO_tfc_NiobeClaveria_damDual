package com.niobe.can_i.usecases.camarero_menu.camarero_home.cesta.metodo_pago

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityMetodoPagoBinding
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.camarero_menu.camarero_home.cesta.metodo_pago.codigoqr.QRCodeActivity
import com.niobe.can_i.usecases.camarero_menu.camarero_home.cesta.metodo_pago.efectivo.EfectivoActivity
import com.niobe.can_i.util.Constants

class MetodoPagoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMetodoPagoBinding
    private lateinit var firebaseUtil: FirebaseUtil
    private var idComanda: String? = null
    private var idCamarero: String? = null
    private var precioTotal: Double = 0.00
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMetodoPagoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializamos FirebaseUtil
        firebaseUtil = FirebaseUtil()

        initUI()

    }

    private fun initUI() {
        idComanda = intent.getStringExtra(Constants.EXTRA_COMANDA)
        idCamarero = intent.getStringExtra(Constants.EXTRA_USUARIO)
        precioTotal = intent.getDoubleExtra(Constants.EXTRA_PRECIO_TOTAL, 0.00)

        if (idComanda.isNullOrEmpty()) {
            Log.e("Error", "El idComanda es inválido o está vacío")
            finish()
            return
        }

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.bEfectivo.setOnClickListener {
            navigateToEfectivo()
        }

        binding.bTarjeta.setOnClickListener {
            navigateToQRCodeActivity()
        }

        createUI()

    }

    private fun navigateToEfectivo() {
        val intent = Intent(this, EfectivoActivity::class.java)
        intent.putExtra(Constants.EXTRA_PRECIO_TOTAL, precioTotal)
        intent.putExtra(Constants.EXTRA_COMANDA, idComanda)
        intent.putExtra(Constants.EXTRA_USUARIO, idCamarero)
        startActivity(intent)
        finish()
    }

    private fun navigateToQRCodeActivity() {
        val intent = Intent(this, QRCodeActivity::class.java).apply {
            putExtra(Constants.EXTRA_COMANDA, idComanda)
            putExtra(Constants.EXTRA_PRECIO_TOTAL, precioTotal)
            putExtra(Constants.EXTRA_USUARIO, idCamarero)
        }
        startActivity(intent)
        finish()
    }

    private fun createUI(){
        binding.tvTotalAPagar.text = buildString {
            append(precioTotal.toString())
            append("€")
        }
    }
}