package com.farmape.backend.trabajadores.dto;

import com.farmape.backend.trabajadores.enums.EstadoTrabajador;

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