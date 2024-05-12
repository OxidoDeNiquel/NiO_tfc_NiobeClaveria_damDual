package com.niobe.can_i.model

data class Usuario (
    var idUsuario: String,
    var email: String,
    var rol: String,
    var nombre: String,
    var apellido1: String,
    var apellido2 : String?,
    var dni: String
)

data class Empleado(
    var idEmpleado: String,
    var usuario: Usuario
)

data class Administrador(
    var idAdministrador: String,
    var usuario: Usuario
)