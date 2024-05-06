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

        room = Room.databaseBuilder(this, CanIDatabase::class.java, "articulo").build()
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
            fillRoomDatabase()
        }
    }
    private fun fillRoomDatabase() {
        Log.i("FILL ROOM DATABASE", "Ha entrado en fillRoomDatabase()")
        val databaseReference = FirebaseDatabase
            .getInstance(Constants.INSTANCE)
            .getReference("articulos")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.i("ONDATACHANGE", "Ha entrado en onDataChange()")
                val articulosList = mutableListOf<Articulo>()
                for (snapshot in dataSnapshot.children) {
                    val articulo = snapshot.getValue(Articulo::class.java)
                    articulo?.let {
                        articulosList.add(it)
                    }
                }

                Log.i("LEYENDO ARTICULOS...", articulosList.toString())

                guardarArticulosEnRoom(articulosList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Log.e("FIREBASE", "Error al leer datos de Firebase: ${databaseError.message}")
            }
        })
    }
    private fun guardarArticulosEnRoom(articulosList: List<Articulo>) {
        Log.i("GUARDAR ARTICULOS ROOM", "Ha entrado en guardarArticulosEnRoom()")
        CoroutineScope(Dispatchers.IO).launch {
            // Eliminar todos los registros de la tabla en el hilo de fondo
            room.articuloDao().deleteAllArticulos()

            // Restablecer la secuencia de autoincremento
            room.articuloDao().resetArticuloAutoincrement()

            // Insertar nuevos registros
            val articuloEntities = articulosList.map { articulo ->
                ArticuloEntity(
                    nombre = articulo.nombre,
                    tipo = articulo.tipo,
                    precio = articulo.precio,
                    stock = articulo.stock
                )
            }
            val dao = room.articuloDao()
            dao.insertAll(articuloEntities)
        }
    }

    suspend fun getArticulosByType(dao: ArticuloDao, tipo: String): List<ArticuloEntity> {
        return dao.getArticuloByType("%${tipo}%")
    }

    private fun leerArticulosTipo(tipoArticulo: String, recyclerView: RecyclerView) {
        Log.i("LEER_ARTICULOS", "Ha entrado en leerArticulosTipo()")
        val adapter = GestionArticulosAdapter()
        Util.setupRecyclerView(this@GestionArticulosActivity, recyclerView, adapter)

        lifecycleScope.launch {
            val articulosList = getArticulosByType(room.articuloDao(), tipoArticulo)
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
    @Deprecated("Deprecated in Java")
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
