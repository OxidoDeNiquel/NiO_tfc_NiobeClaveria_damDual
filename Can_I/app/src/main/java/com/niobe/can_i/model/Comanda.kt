package com.niobe.can_i.model

/**
 * Clase de datos que representa una comanda.
 *
 * @param idComanda Identificador único de la comanda.
 * @param idCamarero Objeto Camarero que representa el camarero asociado a la comanda.
 * @param fechaHora Fecha y hora de la comanda.
 * @param pagada Estado de pago de la comanda.
 */
data class Comanda(
    var idComanda: String = "",
    var idCamarero: Camarero = Camarero(),
    //var idBarra: Barra,
    var fechaHora: String = "",
    var totalComanda: Double = 0.00,
    var pagada: Boolean = false
) {
    // Constructor sin argumentos requerido por Firestore
    constructor() : this("", Camarero(), "", 0.00, false)
}

/**
 * Clase de datos que representa los artículos asociados a una comanda.
 *
 * @param idArticulosComanda Identificador único de la relación entre artículos y comanda.
 * @param idComanda Objeto Comanda que representa la comanda asociada.
 * @param idArticulo Objeto Articulo que representa el artículo asociado.
 * @param cantidad Cantidad del artículo en la comanda.
 */
data class ArticulosComanda(
    var idArticulosComanda: String = "",
    var idComanda: Comanda = Comanda(),
    var idArticulo: Articulo = Articulo(),
    var cantidad: Int = 0
) {
    // Constructor sin argumentos requerido por Firestore
    constructor() : this("", Comanda(), Articulo(), 0)
}
