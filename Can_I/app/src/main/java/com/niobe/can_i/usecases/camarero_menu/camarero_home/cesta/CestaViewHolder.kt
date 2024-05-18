package com.niobe.can_i.usecases.camarero_menu.camarero_home.cesta

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.niobe.can_i.databinding.ItemCestaBinding
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.model.ArticulosComanda

class CestaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemCestaBinding.bind(view)

    fun bind(articulo: Articulo, articuloComanda: ArticulosComanda) {
        binding.tvNombreArticulo.text = articulo.nombre
        binding.tvCantidad.text = buildString {
            append("Cantidad: ")
            append(articuloComanda.cantidad.toString())
        }
        binding.tvPrecio.text = buildString {
            append(articulo.precio.toString())
            append("€")
        }
    }
}
