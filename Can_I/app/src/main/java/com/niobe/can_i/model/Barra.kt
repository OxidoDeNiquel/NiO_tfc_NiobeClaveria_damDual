package com.niobe.can_i.model

data class Barra(
    var idBarra: String = "", // Se puede inicializar con un valor por defecto
    var ubicacion: Int = 0 // Se puede inicializar con un valor por defecto
) {
    // Constructor sin argumentos
    constructor() : this("", 0)
}


data class ArticulosBarra(
    var idArticulosBarra: String,
    var idBarra: Barra,
    var idArticulo: Articulo,
    var cantidad: Int
)
