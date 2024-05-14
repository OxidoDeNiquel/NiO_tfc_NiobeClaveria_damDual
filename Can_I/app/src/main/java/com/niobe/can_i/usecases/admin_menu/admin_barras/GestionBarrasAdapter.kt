package com.niobe.can_i.usecases.admin_menu.admin_barras

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.niobe.can_i.R
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.model.Barra
import com.niobe.can_i.usecases.admin_menu.admin_articulos.GestionArticulosViewHolder

class GestionBarrasAdapter(var barrasList: List<Barra> = emptyList(),
                           private val navigateToDetailActivity: (String) -> Unit) : RecyclerView.Adapter<GestionBarrasViewHolder>(){
    fun updateList(list: List<Barra>) {
        barrasList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GestionBarrasViewHolder {
        return GestionBarrasViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_products_big, parent, false)
        )
    }

    override fun onBindViewHolder(holder: GestionBarrasViewHolder, position: Int) {
        holder.bind(barrasList[position], navigateToDetailActivity)
    }

    override fun getItemCount() = barrasList.size
}
