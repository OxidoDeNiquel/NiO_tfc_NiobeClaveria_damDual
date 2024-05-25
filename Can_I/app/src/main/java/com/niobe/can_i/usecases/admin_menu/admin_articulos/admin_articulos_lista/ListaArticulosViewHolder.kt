package com.niobe.can_i.usecases.admin_menu.admin_articulos.admin_articulos_lista

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.niobe.can_i.databinding.ItemProductsBigBinding
import com.niobe.can_i.model.Articulo

class ListaArticulosViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemProductsBigBinding.bind(view)

    fun bind(articulo: Articulo, navigateToDetailActivity: (String) -> Unit) {
        binding.tvArticulo.text = articulo.nombre
        // Cargar la imagen del artículo si existe una URL válida
        articulo.imagenUrl?.let { url ->
            Glide.with(itemView.context)
                .load(url)
                .into(binding.ivArticulo)
        }

        binding.root.setOnClickListener {
            navigateToDetailActivity(articulo.articuloId)
        }
    }
}