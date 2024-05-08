package com.niobe.can_i.model

data class Articulo(
    var articuloId: String = "",
    var nombre: String = "",
    var tipo: String = "",
    var precio: Double = 0.0,
    var stock: Int = 0
) {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("","", "", 0.0, 0)
}
