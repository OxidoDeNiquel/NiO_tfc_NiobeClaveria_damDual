package com.niobe.can_i.model

/**
 * Clase de datos que representa un camarero.
 *
 * @param idCamarero Identificador único del camarero.
 * @param idEmpleado Objeto Empleado que representa el empleado asociado al camarero.
 * @param idUsuario Objeto Usuario que representa el usuario asociado al camarero.
 * @param idBarraAsignada Objeto Barra que representa la barra asignada al camarero.
 */
data class Camarero(
    var idCamarero: String = "",
    var idEmpleado: Empleado = Empleado(),
    var idUsuario: Usuario = Usuario(),
    var idBarraAsignada: Barra = Barra()
) {
    // Constructor sin argumentos requerido por Firestore
    constructor() : this("", Empleado(), Usuario(), Barra())
}
