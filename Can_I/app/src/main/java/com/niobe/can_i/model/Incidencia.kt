package com.niobe.can_i.model

/**
 * Clase de datos que representa una incidencia.
 *
 * @param idIncidencia Identificador único de la incidencia.
 * @param idArticulo Objeto Articulo que representa el artículo asociado a la incidencia.
 * @param idRepositor Objeto Repositor que representa el repositor asociado a la incidencia.
 * @param fechaHora Fecha y hora de la incidencia.
 */
data class Incidencia(
    var idIncidencia: String,
    var idArticulo: Articulo,
    var idRepositor: Repositor,
    var fechaHora: String
)
