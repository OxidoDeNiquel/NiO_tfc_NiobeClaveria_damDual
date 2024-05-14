package com.niobe.can_i.usecases.camarero_home

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.niobe.can_i.databinding.ItemProductsBinding
import com.niobe.can_i.model.Articulo

class CamareroHomeViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemProductsBinding.bind(view)
    fun bind(articulo: Articulo,
             navigateToDetailActivity: (String) -> Unit) {
        binding.tvArticulo.text = articulo.nombre
        // Enlaza otros datos del artículo a las vistas

        binding.root.setOnClickListener {
            navigateToDetailActivity(articulo.articuloId)
        }
    }
}