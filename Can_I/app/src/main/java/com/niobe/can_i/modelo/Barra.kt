package com.niobe.can_i.modelo

data class Barra(
    var idBarra: String,
    var ubicacion: Int
)

data class ArticulosBarra(
    var idArticulosBarra: String,
    var idBarra: Barra,
    var idArticulo: Articulo,
    var cantidad: Int
)
