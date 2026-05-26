package com.farmape.backend.trabajadores.dto;

import com.farmape.backend.trabajadores.enums.EstadoTrabajador;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoTrabajadorRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoTrabajador estado
) {
}