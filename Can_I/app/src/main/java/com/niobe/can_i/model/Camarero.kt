package com.niobe.can_i.model

data class Camarero(
    var idCamarero: String,
    var idEmpleado: Empleado,
    var idUsuario: Usuario,
    var idBarraAsignada: Barra
)
