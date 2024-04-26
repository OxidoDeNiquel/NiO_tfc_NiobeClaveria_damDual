package com.niobe.can_i.modelo

data class Incidencia(
    var idIncidencia: String,
    var idArticulo: Articulo,
    var idRepositor: Repositor,
    var fechaHora: String
)
