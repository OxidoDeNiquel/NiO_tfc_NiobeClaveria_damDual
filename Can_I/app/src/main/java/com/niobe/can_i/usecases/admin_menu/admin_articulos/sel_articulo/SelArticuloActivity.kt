package com.niobe.can_i.usecases.admin_menu.admin_articulos.sel_articulo

import android.os.Binder
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivitySelArticuloBinding
import com.niobe.can_i.provider.preferences.roomdb.CanIDatabase
import com.niobe.can_i.provider.preferences.roomdb.entities.ArticuloEntity
import com.niobe.can_i.usecases.admin_menu.admin_articulos.GestionArticulosActivity
import com.niobe.can_i.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SelArticuloActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelArticuloBinding
    private lateinit var room: CanIDatabase
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

        room = Room.databaseBuilder(this, CanIDatabase::class.java, "articulo").build()

        val id: Int = intent.getIntExtra(Constants.EXTRA_ID,-1)
        getArticuloInformation(id)
    }

    private fun getArticuloInformation(id: Int) {
        Log.i("ArticuloId", id.toString())
        CoroutineScope(Dispatchers.IO).launch {
            val articulo: ArticuloEntity? = room.articuloDao().getArticuloById(id)

            if(articulo != null){
                runOnUiThread {
                    createUI(articulo)
                }
            }
        }
    }

    private fun createUI(articulo: ArticuloEntity) {
        binding.tvNombreArticulo.text = articulo.nombre
        binding.tvPrecio.text = articulo.precio.toString()
        binding.tvStockValue.text = articulo.stock.toString()
    }
}