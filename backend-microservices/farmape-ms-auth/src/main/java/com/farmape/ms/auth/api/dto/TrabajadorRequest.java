package com.farmape.ms.auth.api.dto;

import com.farmape.ms.auth.domain.model.EstadoTrabajador;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrabajadorRequest(
        @NotBlank(message = "El DNI es obligatorio")
        String dni,

        @NotBlank(message = "Los nombres son obligatorios")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        String apellidos,

        String telefono,

        String direccion,

        @NotNull(message = "El rol es obligatorio")
        Integer idRol,

        EstadoTrabajador estado
) {
}