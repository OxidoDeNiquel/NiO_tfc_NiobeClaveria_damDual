package com.niobe.can_i.usecases.admin_menu.admin_barras

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.niobe.can_i.R
import com.niobe.can_i.model.Barra

class GestionBarrasAdapter(private val context: Context, private val barrasList: List<Barra> = emptyList()) :
    RecyclerView.Adapter<GestionBarrasAdapter.GestionBarrasViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val barrasCollection = firestore.collection("barras")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GestionBarrasViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dialog_barra, parent, false)
        return GestionBarrasViewHolder(view)
    }

    override fun onBindViewHolder(holder: GestionBarrasViewHolder, position: Int) {
        val barra = barrasList[position]
        holder.bind(barra)
    }

    override fun getItemCount() = barrasList.size

    inner class GestionBarrasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvBarra: TextView = itemView.findViewById(R.id.tvBarra)

        init {
            itemView.setOnClickListener {
                // Mostrar el diálogo al hacer clic en un elemento del RecyclerView
                val dialog = Dialog(context)
                dialog.setContentView(R.layout.dialog_barra)

                val tvBarraDialog: TextView = dialog.findViewById(R.id.tvBarra)
                val bBorrarBarra: Button = dialog.findViewById(R.id.bBorrarBarra)
                val bCancelar: Button = dialog.findViewById(R.id.bCancelar)

                val barra = barrasList[adapterPosition]
                tvBarraDialog.text = "Barra ${barra.ubicacion}"

                bBorrarBarra.setOnClickListener {
                    // Lógica para borrar la barra
                    borrarBarra(barra.idBarra)
                    dialog.dismiss()
                }

                bCancelar.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
            }
        }

        fun bind(barra: Barra) {
            tvBarra.text = "Barra ${barra.ubicacion}"
        }
    }

    private fun borrarBarra(idBarra: String) {
        barrasCollection.document(idBarra)
            .delete()
            .addOnSuccessListener {
                println("Barra borrada exitosamente con ID: $idBarra")
            }
            .addOnFailureListener { e ->
                println("Error al borrar la barra con ID: $idBarra: $e")
            }
    }
}
