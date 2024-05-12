package com.niobe.can_i.usecases.admin_menu.admin_barras.sel_barra.articulos_por_barra

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityAdminMenuBinding
import com.niobe.can_i.databinding.ActivityArticulosPorBarraBinding
import com.niobe.can_i.databinding.ActivityCamareroHomeBinding
import com.niobe.can_i.provider.services.firebase.FirebaseUtil
import com.niobe.can_i.usecases.admin_menu.AdminMenuActivity
import com.niobe.can_i.usecases.admin_menu.admin_articulos.admin_articulos_lista.ListaArticulosAdapter
import com.niobe.can_i.usecases.admin_menu.admin_articulos.sel_articulo.SelArticuloActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util

class ArticulosPorBarraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticulosPorBarraBinding
    private val firebaseUtil = FirebaseUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityArticulosPorBarraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUI()
    }

    private fun initUI(){
        val idBarra = intent.getStringExtra(Constants.EXTRA_ARTICULOS_BARRA)
        if(idBarra != null){
            actualizarRecyclerViews(idBarra)
            firebaseUtil.getBarra(idBarra,
                onSuccess = { barra ->
                    if (barra != null) {
                        binding.tvBarras.text = buildString {
                            append("Barra ")
                            append(barra.ubicacion.toString())
                        }
                    } else {
                        Log.e("ArticulosPorBarraActivity", "No se encontró ninguna barra con el ID $idBarra")
                    }
                },
                onFailure = { errorMessage ->
                    Log.e("ArticulosPorBarraActivity", errorMessage)
                }
            )
        } else {
            Log.e("ArticulosPorBarraActivity", "ID de la barra no proporcionado")
        }

        binding.tvInicio.setOnClickListener {
            Util.changeActivity(this, AdminMenuActivity::class.java)
        }
    }

    private fun actualizarRecyclerViews(idBarra: String) {
        val recyclerView = binding.rvBarras
        val adapter = ListaArticulosAdapter { articuloId -> navigateToDetail(articuloId) }
        Util.setupRecyclerViewVertical(this, recyclerView, adapter)

        firebaseUtil.leerArticulosPorIdBarra(idBarra) { articulos ->
            adapter.updateList(articulos)
        }
    }

    private fun navigateToDetail(id: String) {
        val intent = Intent(this, SelArticuloActivity::class.java)
        intent.putExtra(Constants.EXTRA_ID, id)
        startActivity(intent)
    }
}