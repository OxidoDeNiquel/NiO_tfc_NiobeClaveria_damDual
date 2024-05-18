package com.niobe.can_i.usecases.camarero_menu.camarero_home.cesta

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityCestaBinding
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.camarero_menu.camarero_home.sel_articulo.SelArticuloCamareroActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util

class CestaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCestaBinding
    private lateinit var firebaseUtil: FirebaseUtil
    private lateinit var adapter: CestaAdapter
    private var idComanda: String? = null
    private var idCamarero: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCestaBinding.inflate(layoutInflater)
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
        Log.i("IDCOMANDA CESTA", idComanda ?: "")
        idCamarero = intent.getStringExtra(Constants.EXTRA_USUARIO)
        Log.i("IDCAMARERO CESTA", idCamarero ?: "")

        if (idComanda.isNullOrEmpty()) {
            Log.e("Error", "El idComanda es inválido o está vacío")
            finish()
            return
        }

        setupRecyclerView()
        loadArticulosComanda(idComanda!!)

        binding.ivBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = CestaAdapter()
        Util.setupRecyclerViewVertical(this, binding.rvCesta, adapter)
    }

    private fun loadArticulosComanda(idComanda: String) {
        firebaseUtil.getArticulosComandaByComanda(idComanda) { articulosComandaList ->
            Log.i("ArticulosComanda", "Size: ${articulosComandaList.size}")
            adapter.updateList(articulosComandaList)
        }
    }

    private fun navigateToDetail(id: String) {
        val intent = Intent(this, SelArticuloCamareroActivity::class.java)
        intent.putExtra(Constants.EXTRA_ID, id)
        intent.putExtra(Constants.EXTRA_COMANDA, idComanda)
        intent.putExtra(Constants.EXTRA_USUARIO, idCamarero)
        startActivity(intent)
    }
}
