package com.niobe.can_i.modelo

data class Usuario (
    var idUsuario: String,
    var nombreUsuario: String,
    var contrasena: String,
    var tipoUsuario: String,
    var nombre: String,
    var apellido1: String,
    var apellido2: String,
    var dni: String,
    var email: String
)

data class Empleado(
    var idEmpleado: String,
    var usuario: Usuario
)

data class Administrador(
    var idAdministrador: String,
    var usuario: Usuario
)