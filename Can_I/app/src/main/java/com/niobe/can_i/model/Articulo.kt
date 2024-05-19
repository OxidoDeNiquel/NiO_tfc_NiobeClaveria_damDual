package com.niobe.can_i.model

import java.io.Serializable

/**
 * Clase de datos que representa un artículo.
 *
 * @param articuloId Identificador único del artículo.
 * @param nombre Nombre del artículo.
 * @param tipo Tipo del artículo.
 * @param precio Precio del artículo.
 * @param stock Cantidad disponible en stock del artículo.
 * @param imagenUrl URL de la imagen del artículo.
 */
data class Articulo(
    var articuloId: String = "",
    var nombre: String = "",
    var tipo: String = "",
    var precio: Double = 0.0,
    var stock: Int = 0,
    val imagenUrl: String? = null
) : Serializable {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", "", "", 0.0, 0, "")
}

