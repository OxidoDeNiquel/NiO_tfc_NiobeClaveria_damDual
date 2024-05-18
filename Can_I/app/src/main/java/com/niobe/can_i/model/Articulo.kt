package com.niobe.can_i.model

import java.io.Serializable

data class Articulo(
    var articuloId: String = "",
    var nombre: String = "",
    var tipo: String = "",
    var precio: Double = 0.0,
    var stock: Int = 0,
    val imagenUrl: String? = null
) : Serializable{
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("","", "", 0.0, 0, "")
}
