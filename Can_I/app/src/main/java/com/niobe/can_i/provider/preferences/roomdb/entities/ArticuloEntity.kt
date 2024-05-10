package com.niobe.can_i.provider.preferences.roomdb.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niobe.can_i.model.Articulo

@Entity(tableName = "articulos")
data class ArticuloEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val tipo: String,
    val precio: Double,
    val stock: Int
)
fun Articulo.toDatabase() = ArticuloEntity(
    nombre = nombre,
    tipo = tipo,
    precio =  precio,
    stock = stock
)

