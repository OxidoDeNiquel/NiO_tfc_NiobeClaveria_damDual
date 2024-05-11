package com.niobe.can_i.usecases.admin_menu.admin_articulos.admin_articulos_lista

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.niobe.can_i.databinding.ItemProductsBigBinding
import com.niobe.can_i.databinding.ItemProductsBinding
import com.niobe.can_i.model.Articulo

class ListaArticulosViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemProductsBigBinding.bind(view)
    fun bind(articulo: Articulo,
             navigateToDetailActivity: (String) -> Unit) {
        binding.tvArticulo.text = articulo.nombre
        // Enlaza otros datos del artículo a las vistas

        binding.root.setOnClickListener {
            navigateToDetailActivity(articulo.articuloId)
        }
    }
}