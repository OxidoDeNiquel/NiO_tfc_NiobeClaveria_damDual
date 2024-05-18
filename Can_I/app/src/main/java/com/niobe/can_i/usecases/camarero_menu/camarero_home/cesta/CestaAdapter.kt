package com.niobe.can_i.usecases.camarero_menu.camarero_home.cesta

import android.view.LayoutInflater
import android.view.ViewGroup
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.niobe.can_i.R
import com.niobe.can_i.model.Articulo
import com.niobe.can_i.model.ArticulosComanda
import com.niobe.can_i.provider.services.firebase.FirebaseUtil

class CestaAdapter(
    private var articuloComandaList: List<ArticulosComanda> = emptyList()
) : RecyclerView.Adapter<CestaViewHolder>() {

    private val firebaseUtil = FirebaseUtil()

    fun updateList(list: List<ArticulosComanda>) {
        articuloComandaList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CestaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cesta, parent, false)
        return CestaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CestaViewHolder, position: Int) {
        val articuloComanda = articuloComandaList[position]
        firebaseUtil.getArticuloById(articuloComanda.idArticulo.articuloId) { articulo ->
            if (articulo != null) {
                Log.i("Articulo", "Nombre: ${articulo.nombre}, Precio: ${articulo.precio}")
                holder.bind(articulo, articuloComanda)
            } else {
                Log.e("Error", "Artículo no encontrado")
            }
        }
    }

    override fun getItemCount() = articuloComandaList.size
}
