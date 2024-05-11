package com.niobe.can_i.provider.services.firebase

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.usecases.login.LogInActivity
import com.niobe.can_i.util.Constants

class FirebaseUtil {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance(Constants.INSTANCE)
        .getReference("articulos")

    fun cerrarSesion(context: Context) {
        auth.signOut()
        // Redirigir al usuario de vuelta a la pantalla de inicio de sesión
        val intent = Intent(context, LogInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    fun leerArticulos(tipoArticulo: String, callback: (List<Articulo>) -> Unit) {
        val query = databaseReference.orderByChild("tipo").equalTo(tipoArticulo)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val articulos: MutableList<Articulo> = mutableListOf()
                for (childSnapshot in dataSnapshot.children) {
                    val articuloId = childSnapshot.child("articuloId").getValue(String::class.java) ?: ""
                    val nombre = childSnapshot.child("nombre").getValue(String::class.java) ?: ""
                    val tipo = childSnapshot.child("tipo").getValue(String::class.java) ?: ""
                    val precio = childSnapshot.child("precio").getValue(Double::class.java) ?: 0.0
                    val stock = childSnapshot.child("stock").getValue(Int::class.java) ?: 0
                    val articulo = Articulo(articuloId, nombre, tipo, precio, stock)
                    articulos.add(articulo)
                }
                callback(articulos)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("ERROR", "Error al leer datos de Firebase: ${databaseError.message}")
            }
        })
    }
    fun deleteArticulo(articuloId: String, callback: (Boolean) -> Unit) {
        databaseReference.orderByChild("articuloId").equalTo(articuloId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val articuloSnapshot = dataSnapshot.children.firstOrNull()
                        val key = articuloSnapshot?.key
                        if (key != null) {
                            databaseReference.child(key).removeValue()
                                .addOnSuccessListener {
                                    callback(true)
                                }
                                .addOnFailureListener {
                                    callback(false)
                                }
                        } else {
                            callback(false)
                        }
                    } else {
                        callback(false)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    callback(false)
                }
            })
    }

    fun getArticulo(articuloId: String, onSuccess: (Articulo?) -> Unit, onFailure: (String) -> Unit) {
        val query = databaseReference.orderByChild("articuloId").equalTo(articuloId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val articuloSnapshot = dataSnapshot.children.firstOrNull()
                    val articulo = articuloSnapshot?.getValue(Articulo::class.java)
                    onSuccess(articulo)
                } else {
                    onFailure("No se encontró ningún artículo con el ID $articuloId")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onFailure("Error al obtener el artículo: ${databaseError.message}")
            }
        })
    }


    fun guardarArticulo(articulo: Articulo, callback: (Boolean, String) -> Unit) {
        val key = databaseReference.push().key
        if (key != null) {
            databaseReference.child(key).setValue(articulo)
                .addOnSuccessListener {
                    callback(true, key)
                }
                .addOnFailureListener {
                    callback(false, "")
                }
        } else {
            callback(false, "")
        }
    }
    fun updateArticulo(articuloId: String, nuevoArticulo: Articulo, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val articuloRef = databaseReference.orderByChild("articuloId").equalTo(articuloId)
        articuloRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val articuloSnapshot = dataSnapshot.children.firstOrNull()
                    if (articuloSnapshot != null) {
                        articuloSnapshot.ref.updateChildren(
                            mapOf(
                                "nombre" to nuevoArticulo.nombre,
                                "tipo" to nuevoArticulo.tipo,
                                "precio" to nuevoArticulo.precio,
                                "stock" to nuevoArticulo.stock
                            )
                        ).addOnSuccessListener {
                            onSuccess()
                        }.addOnFailureListener {
                            onFailure("Error al actualizar el artículo: ${it.message}")
                        }
                    } else {
                        onFailure("No se encontró el artículo con el ID $articuloId")
                    }
                } else {
                    onFailure("No se encontró ningún artículo con el ID $articuloId")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onFailure("Error al obtener el artículo: ${databaseError.message}")
            }
        })
    }

}