package com.niobe.can_i.model

data class Comanda(
    var idComanda: String,
    var idCamarero: Camarero,
    var idBarra: Barra,
    var fechaHora: String
)

data class ArticulosComanda(
    var idArticulosComanda: String,
    var idComanda: Comanda,
    var idArticulo: Articulo,
    var cantidad: Int
)
