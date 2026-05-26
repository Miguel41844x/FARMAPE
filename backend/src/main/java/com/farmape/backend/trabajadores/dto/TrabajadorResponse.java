package com.farmape.backend.trabajadores.dto;

import com.farmape.backend.trabajadores.enums.EstadoTrabajador;

import java.time.LocalDateTime;

public record TrabajadorResponse(
        Integer idTrabajador,
        Integer idRol,
        String rol,
        String dni,
        String nombres,
        String apellidos,
        String telefono,
        String direccion,
        EstadoTrabajador estado,
        LocalDateTime fechaRegistro
) {
}