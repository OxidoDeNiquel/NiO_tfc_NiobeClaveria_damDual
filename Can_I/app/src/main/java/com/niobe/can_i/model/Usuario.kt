package com.niobe.can_i.model

import java.io.Serializable

/**
 * Clase de datos que representa un usuario.
 *
 * @param idUsuario Identificador único del usuario.
 * @param email Correo electrónico del usuario.
 * @param rol Rol del usuario.
 * @param nombre Nombre del usuario.
 * @param apellido1 Primer apellido del usuario.
 * @param apellido2 Segundo apellido del usuario.
 * @param dni DNI del usuario.
 */
data class Usuario(
    var idUsuario: String,
    var email: String,
    var rol: String,
    var nombre: String,
    var apellido1: String,
    var apellido2: String?,
    var dni: String
) : Serializable {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", "", "", "", "", "", "")
}

/**
 * Clase de datos que representa un empleado.
 *
 * @param idEmpleado Identificador único del empleado.
 * @param usuario Objeto Usuario que representa el usuario asociado al empleado.
 */
data class Empleado(
    var idEmpleado: String = "",
    var usuario: Usuario = Usuario()
) {
    // Constructor sin argumentos requerido por Firestore
    constructor() : this("", Usuario())
}

/**
 * Clase de datos que representa un administrador.
 *
 * @param idAdministrador Identificador único del administrador.
 * @param usuario Objeto Usuario que representa el usuario asociado al administrador.
 */
data class Administrador(
    var idAdministrador: String = "",
    var usuario: Usuario = Usuario()
) {
    constructor() : this("", Usuario())
}
