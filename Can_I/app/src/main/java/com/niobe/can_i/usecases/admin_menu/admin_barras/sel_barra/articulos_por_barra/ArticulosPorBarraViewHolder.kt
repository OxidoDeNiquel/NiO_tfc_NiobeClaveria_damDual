package com.niobe.can_i.usecases.admin_menu.admin_barras.sel_barra.articulos_por_barra

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.niobe.can_i.databinding.ItemProductsBigBinding
import com.niobe.can_i.model.Articulo

class ArticulosPorBarraViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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