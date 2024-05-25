package com.niobe.can_i.usecases.admin_menu.admin_articulos.admin_articulos_lista

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.niobe.can_i.R
import com.niobe.can_i.model.Articulo

class ListaArticulosAdapter(
    private var articuloList: List<Articulo> = emptyList(),
    private val navigateToDetail: (String) -> Unit
) : RecyclerView.Adapter<ListaArticulosViewHolder>() {

    fun updateList(list: List<Articulo>) {
        articuloList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaArticulosViewHolder {
        return ListaArticulosViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_products_big, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListaArticulosViewHolder, position: Int) {
        holder.bind(articuloList[position], navigateToDetail)
    }

    override fun getItemCount() = articuloList.size
}