package com.farmape.ms.auth.api.dto;

import com.farmape.ms.auth.domain.model.EstadoTrabajador;

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