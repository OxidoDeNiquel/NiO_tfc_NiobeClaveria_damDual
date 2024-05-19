package com.niobe.can_i.usecases.camarero_menu.camarero_home.cesta.metodo_pago.resultado_operacion

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityOkBinding
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.camarero_menu.CamareroMenuActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util

class OK_Activity : AppCompatActivity() {

    private lateinit var binding: ActivityOkBinding
    private lateinit var firebaseUtil: FirebaseUtil
    private var idComanda: String? = null
    private var precioTotal: Double = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOkBinding.inflate(layoutInflater)
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
        Log.i("IDCOMANDA OK", idComanda ?: "")
        precioTotal = intent.getDoubleExtra(Constants.EXTRA_PRECIO_TOTAL, 0.00)
        Log.i("PRECIOTOTAL OK", precioTotal.toString())

        idComanda?.let {
            updateComandaPagado(it)
        }

        binding.bVolverMenu.setOnClickListener {
            Util.changeActivity(this, CamareroMenuActivity::class.java)
        }
    }

    private fun updateComandaPagado(idComanda: String) {
        firebaseUtil.updateComandaPagado(idComanda, true) { success ->
            if (success) {
                Log.i("UPDATECOMANDA", "Comanda actualizada a pagado")
            } else {
                Log.e("UPDATECOMANDA", "Error actualizando comanda")
            }
        }
    }
}
