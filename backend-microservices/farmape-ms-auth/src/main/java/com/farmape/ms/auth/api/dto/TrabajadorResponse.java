package com.farmape.ms.auth.trabajadores.dto;

import com.farmape.ms.auth.trabajadores.enums.EstadoTrabajador;

public record TrabajadorResponse(
        Integer idTrabajador,
        String dni,
        String nombres,
        String apellidos,
        String telefono,
        String direccion,
        Integer idRol,
        String rol,
        EstadoTrabajador estado
) {
}