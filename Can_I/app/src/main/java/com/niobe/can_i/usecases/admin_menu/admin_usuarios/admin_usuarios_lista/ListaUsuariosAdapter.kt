package com.niobe.can_i.usecases.admin_menu.admin_usuarios.admin_usuarios_lista

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.niobe.can_i.R
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.model.Usuario

class ListaUsuariosAdapter (var usuariosList: List<Usuario> = emptyList(),
                            private val navigateToList: (String) -> Unit) : RecyclerView.Adapter<ListaUsuariosViewHolder>() {
    fun updateList(list: List<Usuario>) {
        usuariosList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaUsuariosViewHolder {
        return ListaUsuariosViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListaUsuariosViewHolder, position: Int) {
        holder.bind(usuariosList[position], navigateToList)
    }

    override fun getItemCount() = usuariosList.size
}