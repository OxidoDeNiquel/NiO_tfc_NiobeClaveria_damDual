package com.niobe.can_i.model

data class Comanda(
    var idComanda: String = "",
    var idCamarero: Camarero = Camarero(),
    //var idBarra: Barra,
    var fechaHora: String = "",
    var pagada: Boolean = false
) {
    // Constructor sin argumentos requerido por Firestore
    constructor() : this("", Camarero(), "", false)
}

data class ArticulosComanda(
    var idArticulosComanda: String = "",
    var idComanda: Comanda = Comanda(),
    var idArticulo: Articulo = Articulo(),
    var cantidad: Int = 0
) {
    // Constructor sin argumentos requerido por Firestore
    constructor() : this("", Comanda(), Articulo(), 0)
}


