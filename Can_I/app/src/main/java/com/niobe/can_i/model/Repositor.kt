package com.niobe.can_i.model

/**
 * Clase de datos que representa un repositor.
 *
 * @param idRepositor Identificador único del repositor.
 * @param idEmpleado Objeto Empleado que representa el empleado asociado al repositor.
 * @param idUsuario Objeto Usuario que representa el usuario asociado al repositor.
 */
data class Repositor(
    var idRepositor: String,
    var idEmpleado: Empleado,
    var idUsuario: Usuario
)
