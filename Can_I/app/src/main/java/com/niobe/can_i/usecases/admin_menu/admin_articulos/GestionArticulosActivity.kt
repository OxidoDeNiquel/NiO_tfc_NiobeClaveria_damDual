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
import com.niobe.can_i.usecases.admin_menu.admin_articulos.sel_articulo.SelArticuloActivity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GestionArticulosActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGestionArticulosBinding

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

        //Inicializamos los componentes
        initUI()
    }

    //Función para inicializar todos los componentes del layout
    private fun initUI(){
        // Configuramos el click listener para el botón
        binding.bAnadirArticulo.setOnClickListener {
            Util.changeActivity(this, CrearArticuloActivity::class.java)
        }
        binding.tvInicio.setOnClickListener {
            Util.changeActivity(this, AdminMenuActivity::class.java)
        }

        // Llenar la base de datos de Room
        //llenarBDRoom()
        // Leer y mostrar los artículos por tipo en los RecyclerViews
        onResume()
    }

    override fun onResume() {
        super.onResume()
        actualizarRecyclerViews()
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

    private fun actualizarRecyclerViews() {
        leerArticulos(Constants.TIPO_ARTICULO_CERVEZA, binding.rvCervezas)
        leerArticulos(Constants.TIPO_ARTICULO_COPA, binding.rvCopa)
        leerArticulos(Constants.TIPO_ARTICULO_SIN_ALCOHOL, binding.rvSinAlcohol)
    }

    private fun leerArticulos(tipoArticulo: String, recyclerView: RecyclerView) {
        Log.i("FUNCION", "He entrado a la funcion")
        val adapter = GestionArticulosAdapter { articuloId -> navigateToDetail(articuloId) }
        Util.setupRecyclerView(this@GestionArticulosActivity, recyclerView, adapter)

        // Obtener una referencia a la base de datos
        val databaseReference = FirebaseDatabase
            .getInstance(Constants.INSTANCE)
            .getReference("articulos")

        Log.i("databaseReference", databaseReference.toString())

        // Definir la consulta para filtrar los artículos por tipo
        val query = databaseReference.orderByChild("tipo").equalTo(tipoArticulo)

        // Agregar un listener para manejar los resultados de la consulta
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Crear una lista mutable para almacenar los artículos
                val articulos: MutableList<Articulo> = mutableListOf()
                Log.i("dataSnapshot", dataSnapshot.toString())
                // Iterar sobre los resultados y convertirlos en objetos Articulo
                for (childSnapshot in dataSnapshot.children) {
                    Log.i("childSnapshot", childSnapshot.toString())
                    // Obtener los datos del artículo
                    val articuloId = childSnapshot.child("articuloId").getValue(String::class.java) ?: ""
                    val nombre = childSnapshot.child("nombre").getValue(String::class.java) ?: ""
                    val tipo = childSnapshot.child("tipo").getValue(String::class.java) ?: ""
                    val precio = childSnapshot.child("precio").getValue(Double::class.java) ?: 0.0
                    val stock = childSnapshot.child("stock").getValue(Int::class.java) ?: 0

                    // Crear un objeto Articulo y agregarlo a la lista
                    val articulo = Articulo(articuloId, nombre, tipo, precio, stock)
                    articulos.add(articulo)
                }
                Log.i("articulos", articulos.toString())
                // Actualizar el RecyclerView con la lista de artículos
                adapter.updateList(articulos)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar errores de lectura de la base de datos
                Log.e("ERROR", "Error al leer datos de Firebase: ${databaseError.message}")
            }
        })
    }
    private fun navigateToDetail(id: String) {
        val intent = Intent(this, SelArticuloActivity::class.java)
        intent.putExtra(Constants.EXTRA_ID, id)
        startActivity(intent)
    }
}
