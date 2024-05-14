package com.niobe.can_i.usecases.admin_menu.admin_usuarios

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.niobe.can_i.databinding.ItemProductsBinding
import com.niobe.can_i.databinding.ItemUsuarioBinding
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.model.Usuario

class GestionUsuariosViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemUsuarioBinding.bind(view)
    fun bind(usuario: Usuario,
             navigateToDetailActivity: (String) -> Unit) {
        binding.tvNombreUsuario.text = buildString {
            append(usuario.nombre)
            append(" ")
            append(usuario.apellido1)
        }
        binding.tvPuesto.text = buildString {
            append("Puesto: ")
            append(usuario.rol)
        }
        // Enlaza otros datos del artículo a las vistas

        binding.root.setOnClickListener {
            navigateToDetailActivity(usuario.idUsuario)
        }
    }
}