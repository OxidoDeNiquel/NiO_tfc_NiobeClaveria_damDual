package com.niobe.can_i.model

/**
 * Clase de datos que representa una barra.
 *
 * @param idBarra Identificador único de la barra.
 * @param ubicacion Ubicación de la barra.
 */
data class Barra(
    var idBarra: String = "", // Se puede inicializar con un valor por defecto
    var ubicacion: Int = 0 // Se puede inicializar con un valor por defecto
) {
    // Constructor sin argumentos
    constructor() : this("", 0)
}

/**
 * Clase de datos que representa los artículos asociados a una barra.
 *
 * @param idArticulosBarra Identificador único de la relación entre artículos y barra.
 * @param idBarra Objeto Barra que representa la barra asociada.
 * @param idArticulo Objeto Articulo que representa el artículo asociado.
 * @param cantidad Cantidad del artículo en la barra.
 */
data class ArticulosBarra(
    var idArticulosBarra: String,
    var idBarra: Barra,
    var idArticulo: Articulo,
    var cantidad: Int
)
