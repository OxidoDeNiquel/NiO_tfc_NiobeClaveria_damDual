/**
 * Clase de utilidad que proporciona funciones comunes para realizar tareas específicas.
 */
package com.niobe.can_i.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.Calendar
import java.util.Currency
import java.util.Locale

object Util {
    /**
     * Configura un RecyclerView con un LinearLayoutManager horizontal y un adaptador dado.
     *
     * @param activity Actividad que contiene el RecyclerView.
     * @param recyclerView RecyclerView que se va a configurar.
     * @param adapter Adaptador que se va a establecer en el RecyclerView.
     */
    fun setupRecyclerViewHorizontal(activity: AppCompatActivity, recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        // Establece el RecyclerView con un tamaño fijo y un LayoutManager horizontal
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        // Asigna el adaptador al RecyclerView
        recyclerView.adapter = adapter
    }

    /**
     * Configura un RecyclerView con un LinearLayoutManager vertical y un adaptador dado.
     *
     * @param activity Actividad que contiene el RecyclerView.
     * @param recyclerView RecyclerView que se va a configurar.
     * @param adapter Adaptador que se va a establecer en el RecyclerView.
     */
    fun setupRecyclerViewVertical(activity: AppCompatActivity, recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        // Establece el RecyclerView con un tamaño fijo y un LayoutManager vertical
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        // Asigna el adaptador al RecyclerView
        recyclerView.adapter = adapter
    }

    /**
     * Cambia la actividad actual a la especificada por la clase de destino y finaliza la actividad actual.
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
     * Cambia la actividad actual a la especificada por la clase de destino sin finalizar la actividad actual.
     *
     * @param context Contexto de la actividad actual.
     * @param destination Clase de la actividad de destino.
     */
    fun changeActivityWithoutFinish(context: Context, uidAuth: String, destination: Class<*>) {
        // Crea un intent para la nueva actividad
        val intent = Intent(context, destination)
        intent.putExtra(Constants.EXTRA_USUARIO, uidAuth)
        // Inicia la nueva actividad
        context.startActivity(intent)
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

    /**
     * Muestra un mensaje Toast corto en el contexto especificado.
     *
     * @param context Contexto en el que se mostrará el Toast.
     * @param message Mensaje a mostrar en el Toast.
     */
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Obtiene la fecha y hora actuales como una cadena de texto en formato predeterminado.
     *
     * @return Cadena de texto que representa la fecha y hora actuales.
     */
    fun obtenerFechaHoraActual(): String {
        val currentDateTime = Calendar.getInstance().time
        return currentDateTime.toString() // Puedes formatear esto según tus necesidades
    }

    /**
     * Formatea un valor numérico como una cadena de texto en la moneda europea (EUR).
     *
     * @param value Valor numérico a formatear.
     * @return Cadena de texto que representa el valor formateado en la moneda europea.
     */
    fun formatCurrency(value: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale.GERMANY)
        format.currency = Currency.getInstance("EUR")
        return format.format(value)
    }
}
