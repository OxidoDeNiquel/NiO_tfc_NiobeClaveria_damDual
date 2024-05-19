package com.niobe.can_i.usecases.admin_menu.admin_usuarios.admin_usuarios_lista

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.niobe.can_i.databinding.ItemProductsBigBinding
import com.niobe.can_i.databinding.ItemUsuarioBinding
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.model.Usuario

class ListaUsuariosViewHolder (view: View) : RecyclerView.ViewHolder(view) {
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

        binding.root.setOnClickListener {
            navigateToDetailActivity(usuario.idUsuario)
        }
    }
}