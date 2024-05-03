package com.niobe.can_i.usecases.admin_menu.admin_articulos

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.niobe.can_i.database.entity.ArticulosEntity
import com.niobe.can_i.databinding.ItemProductsBinding

class GestionArticulosViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private var binding = ItemProductsBinding.bind(view)

    fun bind(articulosEntityResponse: ArticulosEntity){
        binding.tvArticulo.text = articulosEntityResponse.nombre
    }
}