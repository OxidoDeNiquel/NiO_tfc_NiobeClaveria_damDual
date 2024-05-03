package com.niobe.can_i.model

data class Articulo(
    var idArticulo: String? = null,
    var nombre: String,
    var precio: Double,
    var stock: Int
)