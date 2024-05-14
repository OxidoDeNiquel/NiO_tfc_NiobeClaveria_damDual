package com.niobe.can_i.usecases.admin_menu.admin_usuarios

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.niobe.can_i.R
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.model.Usuario
import com.niobe.can_i.usecases.admin_menu.admin_articulos.GestionArticulosViewHolder

class GestionUsuariosAdapter(var usuarioList: List<Usuario> = emptyList(),
                             private val navigateToDetailActivity: (String) -> Unit) : RecyclerView.Adapter<GestionUsuariosViewHolder>() {
    fun updateList(list: List<Usuario>) {
        usuarioList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GestionUsuariosViewHolder {
        return GestionUsuariosViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false)
        )
    }

    override fun onBindViewHolder(holder: GestionUsuariosViewHolder, position: Int) {
        holder.bind(usuarioList[position], navigateToDetailActivity)
    }

    override fun getItemCount() = usuarioList.size
}