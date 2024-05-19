package com.niobe.can_i.usecases.camarero_menu.camarero_home.cesta.metodo_pago.efectivo

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityEfectivoBinding
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.camarero_menu.camarero_home.cesta.metodo_pago.resultado_operacion.OK_Activity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util

class EfectivoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEfectivoBinding
    private lateinit var firebaseUtil: FirebaseUtil
    private var idComanda: String? = null
    private var precioTotal: Double = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEfectivoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializamos FirebaseUtil
        firebaseUtil = FirebaseUtil()

        initUI()
        createUI()
        setupListeners()
    }

    private fun initUI() {
        idComanda = intent.getStringExtra(Constants.EXTRA_COMANDA)
        Log.i("IDCOMANDA EFECTIVO", idComanda ?: "")
        precioTotal = intent.getDoubleExtra(Constants.EXTRA_PRECIO_TOTAL, 0.00)
        Log.i("PRECIOTOTAL EFECTIVO", precioTotal.toString())

        if (idComanda.isNullOrEmpty()) {
            Log.e("Error", "El idComanda es inválido o está vacío")
            finish()
            return
        }

        binding.bConfirmar.setOnClickListener {
            navigateToOK()
        }

        binding.bCancelar.setOnClickListener {
            finish()
        }
    }

    private fun createUI() {
        binding.tvTotalAPagar.text = Util.formatCurrency(precioTotal)
    }

    private fun setupListeners() {
        binding.tvTotalEntregado.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateChange()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun calculateChange() {
        val entregadoText = binding.tvTotalEntregado.text.toString().replace(",", ".").replace("€", "").trim()
        val entregado = entregadoText.toDoubleOrNull() ?: 0.0
        val cambio = entregado - precioTotal
        binding.tvTotalCambio.text = Util.formatCurrency(cambio)
    }

    private fun navigateToOK() {
        val intent = Intent(this, OK_Activity::class.java)
        intent.putExtra(Constants.EXTRA_PRECIO_TOTAL, precioTotal)
        intent.putExtra(Constants.EXTRA_COMANDA, idComanda)
        startActivity(intent)
        finish()
    }
}
