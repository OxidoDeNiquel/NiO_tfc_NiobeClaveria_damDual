package com.niobe.can_i.usecases.admin_menu.admin_articulos

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.niobe.can_i.databinding.ItemProductsBinding
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.provider.preferences.roomdb.entities.ArticuloEntity
class GestionArticulosViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemProductsBinding.bind(view)
    fun bind(articuloEntity: ArticuloEntity,
             navigateToDetailActivity: (Int) -> Unit) {
        binding.tvArticulo.text = articuloEntity.nombre
        // Enlaza otros datos del artículo a las vistas

        binding.root.setOnClickListener {
            navigateToDetailActivity(articuloEntity.id)
        }
    }
}
