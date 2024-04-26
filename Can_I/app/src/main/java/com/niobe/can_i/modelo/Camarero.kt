package com.niobe.can_i.modelo

data class Camarero(
    var idCamarero: String,
    var idEmpleado: Empleado,
    var idUsuario: Usuario,
    var idBarraAsignada: Barra
)
