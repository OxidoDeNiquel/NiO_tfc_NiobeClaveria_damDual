package com.niobe.can_i.provider.preferences

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.provider.preferences.roomdb.CanIDatabase
import com.niobe.can_i.provider.preferences.roomdb.dao.ArticuloDao
import com.niobe.can_i.provider.preferences.roomdb.entities.ArticuloEntity
import com.niobe.can_i.util.Constants
import com.niobe.can_i.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CanIRepository (private val room: CanIDatabase){

    suspend fun deleteAllArticulos() {
        room.articuloDao().deleteAllArticulos()
    }

    suspend fun fillRoomDatabase() {
        val databaseReference = Util.getDatabaseReference()
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
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
}