package com.niobe.can_i.util

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.niobe.can_i.usecases.admin_menu.admin_articulos.GestionArticulosActivity

object Util {
    /**
     * Configura un RecyclerView con un LinearLayoutManager horizontal y un adaptador dado.
     *
     * @param activity Actividad que contiene el RecyclerView.
     * @param recyclerView RecyclerView que se va a configurar.
     * @param adapter Adaptador que se va a establecer en el RecyclerView.
     */
    fun setupRecyclerView(activity: AppCompatActivity, recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        // Establece el RecyclerView con un tamaño fijo y un LayoutManager horizontal
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        // Asigna el adaptador al RecyclerView
        recyclerView.adapter = adapter
    }

    /**
     * Cambia la actividad actual a la especificada por la clase de destino.
     *
     * @param context Contexto de la actividad actual.
     * @param destination Clase de la actividad de destino.
     */
    fun changeActivity(context: Context, destination: Class<*>) {
        // Crea un intent para la nueva actividad
        val intent = Intent(context, destination)
        // Inicia la nueva actividad
        context.startActivity(intent)
        // Finaliza la actividad actual si es una AppCompatActivity
        if (context is AppCompatActivity) {
            context.finish()
        }
    }

    /**
     * Obtiene una referencia a la base de datos Firebase.
     *
     * @return Referencia a la base de datos de Firebase.
     */
    fun getDatabaseReference(): DatabaseReference {
        // Obtiene la referencia a la base de datos Firebase
        return FirebaseDatabase
            .getInstance("https://can-i-oxidodeniquel-2024-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("articulos")
    }

}