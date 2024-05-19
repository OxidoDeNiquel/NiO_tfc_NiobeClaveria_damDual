package com.niobe.can_i.model

import java.io.Serializable

data class Usuario (
    var idUsuario: String,
    var email: String,
    var rol: String,
    var nombre: String,
    var apellido1: String,
    var apellido2 : String?,
    var dni: String
) : Serializable {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("","", "", "", "", "", "")
}


data class Empleado(
    var idEmpleado: String = "",
    var usuario: Usuario = Usuario()
) {
    // Constructor sin argumentos requerido por Firestore
    constructor() : this("", Usuario())
}

data class Administrador(
    var idAdministrador: String = "",
    var usuario: Usuario = Usuario()
){
    constructor() : this("", Usuario())
}