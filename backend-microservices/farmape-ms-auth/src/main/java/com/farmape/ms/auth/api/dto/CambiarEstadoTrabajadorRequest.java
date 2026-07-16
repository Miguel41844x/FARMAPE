package com.farmape.ms.auth.api.dto;

import com.farmape.ms.auth.domain.model.EstadoTrabajador;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoTrabajadorRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoTrabajador estado
) {
}