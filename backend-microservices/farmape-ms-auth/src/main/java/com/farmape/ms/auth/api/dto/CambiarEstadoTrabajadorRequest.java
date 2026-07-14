package com.farmape.ms.auth.trabajadores.dto;

import com.farmape.ms.auth.trabajadores.enums.EstadoTrabajador;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoTrabajadorRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoTrabajador estado
) {
}