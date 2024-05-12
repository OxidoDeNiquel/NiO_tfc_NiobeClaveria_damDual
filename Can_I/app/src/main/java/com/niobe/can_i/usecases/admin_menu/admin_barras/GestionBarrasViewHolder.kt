package com.niobe.can_i.usecases.admin_menu.admin_barras

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.niobe.can_i.databinding.ItemProductsBigBinding
import com.niobe.can_i.databinding.ItemProductsBinding
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.model.Barra

class GestionBarrasViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemProductsBinding.bind(view)
    fun bind(barra: Barra,
             navigateToDetailActivity: (String) -> Unit) {
        binding.tvArticulo.text = buildString {
            append("Barra ")
            append(barra.ubicacion)
        }
        // Enlaza otros datos del artículo a las vistas

        binding.root.setOnClickListener {
            navigateToDetailActivity(barra.idBarra)
        }
    }
}
