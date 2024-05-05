package com.niobe.can_i.usecases.admin_menu.admin_articulos

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.niobe.can_i.R
import com.niobe.can_i.databinding.ActivityGestionArticulosBinding
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.provider.preferences.CanIRepository
import com.niobe.can_i.provider.preferences.roomdb.CanIDatabase
import com.niobe.can_i.provider.preferences.roomdb.dao.ArticuloDao
import com.niobe.can_i.provider.preferences.roomdb.entities.ArticuloEntity
import com.niobe.can_i.usecases.admin_menu.AdminMenuActivity
import com.niobe.can_i.usecases.admin_menu.admin_articulos.crear_articulo.CrearArticuloActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GestionArticulosActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGestionArticulosBinding
    private lateinit var botonCrear: Button
    private lateinit var room: CanIDatabase
    private lateinit var repository: CanIRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //Inicializamos el binding
        binding = ActivityGestionArticulosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        room = Room.databaseBuilder(this, CanIDatabase::class.java, "movies_table").build()
        repository = CanIRepository(room)

        //Inicializamos los componentes
        initUI()
    }

    //Función para inicializar todos los componentes del layout
    private fun initUI(){
        botonCrear = binding.bAnadirArticulo
        // Configuramos el click listener para el botón
        botonCrear.setOnClickListener {
            Util.changeActivity(this, CrearArticuloActivity::class.java)
        }
        binding.tvInicio.setOnClickListener {
            Util.changeActivity(this, AdminMenuActivity::class.java)
        }
        // Llenar la base de datos de Room
        llenarBDRoom()
        // Leer y mostrar los artículos por tipo en los RecyclerViews
        onResume()
    }

    private fun llenarBDRoom() {
        lifecycleScope.launch {
            repository.fillRoomDatabase()
        }
    }

    private fun leerArticulosTipo(tipoArticulo: String, recyclerView: RecyclerView) {
        Log.i("LEER_ARTICULOS", "Ha entrado en leerArticulosTipo()")
        val adapter = GestionArticulosAdapter()
        Util.setupRecyclerView(this@GestionArticulosActivity, recyclerView, adapter)

        lifecycleScope.launch {
            val articulosList = repository.getArticulosByType(room.articuloDao(), tipoArticulo)
            Log.i("HILO", "Ha entrado en el hilo")
            // Configurar el adapter y asignarlo al RecyclerView correspondiente
            adapter.updateList(articulosList)
        }
    }

    override fun onResume() {
        Log.i("ONRESUME", "Ha entrado en OnResume")
        super.onResume()
        actualizarRecyclerViews()
    }

    private fun actualizarRecyclerViews() {
        Log.i("ACTUALIZAR_RV", "Ha entrado en actualizarRecyclerViews()")
        leerArticulosTipo(Constants.TIPO_ARTICULO_CERVEZA, binding.rvCervezas)
        leerArticulosTipo(Constants.TIPO_ARTICULO_COPA, binding.rvCopa)
        leerArticulosTipo(Constants.TIPO_ARTICULO_SIN_ALCOHOL, binding.rvSinAlcohol)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_CODE_CREAR_ARTICULO && resultCode == Activity.RESULT_OK) {
            // Obtener la clave del artículo del intent resultante
            val articuloId = data?.getStringExtra("articuloId")
            // Verificar que la clave del artículo no sea nula
            if (articuloId != null) {
                // Actualizar los RecyclerViews utilizando la clave del artículo
                actualizarRecyclerViews()
            }
        }
    }
}
