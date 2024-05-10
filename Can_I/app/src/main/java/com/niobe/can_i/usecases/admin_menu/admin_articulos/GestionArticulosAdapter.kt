package com.niobe.can_i.usecases.admin_menu.admin_articulos

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.niobe.can_i.R
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.provider.preferences.roomdb.entities.ArticuloEntity

class GestionArticulosAdapter(var articuloList: List<Articulo> = emptyList(),
                              private val navigateToDetailActivity: (String) -> Unit) : RecyclerView.Adapter<GestionArticulosViewHolder>() {
    fun updateList(list: List<Articulo>) {
        articuloList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GestionArticulosViewHolder {
        return GestionArticulosViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_products, parent, false)
        )
    }

    override fun onBindViewHolder(holder: GestionArticulosViewHolder, position: Int) {
        holder.bind(articuloList[position], navigateToDetailActivity)
    }

    override fun getItemCount() = articuloList.size
}