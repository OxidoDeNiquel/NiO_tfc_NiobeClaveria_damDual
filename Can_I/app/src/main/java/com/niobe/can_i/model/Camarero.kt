package com.niobe.can_i.model

data class Camarero(
    var idCamarero: String = "",
    var idEmpleado: Empleado = Empleado(),
    var idUsuario: Usuario = Usuario(),
    var idBarraAsignada: Barra = Barra()
) {
    // Constructor sin argumentos requerido por Firestore
    constructor() : this("", Empleado(), Usuario(), Barra())
}


