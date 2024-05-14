package com.niobe.can_i.usecases.camarero_home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.niobe.can_i.R
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.usecases.admin_menu.admin_articulos.GestionArticulosViewHolder

class CamareroHomeAdapter (var articuloList: List<Articulo> = emptyList(),
                           private val navigateToDetailActivity: (String) -> Unit) : RecyclerView.Adapter<CamareroHomeViewHolder>() {
    fun updateList(list: List<Articulo>) {
        articuloList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CamareroHomeViewHolder {
        return CamareroHomeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_products, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CamareroHomeViewHolder, position: Int) {
        holder.bind(articuloList[position], navigateToDetailActivity)
    }

    override fun getItemCount() = articuloList.size
}