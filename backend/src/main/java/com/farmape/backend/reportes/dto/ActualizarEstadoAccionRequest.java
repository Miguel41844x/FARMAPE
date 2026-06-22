package com.farmape.backend.reportes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActualizarEstadoAccionRequest(
        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 30, message = "El estado no debe superar 30 caracteres")
        String estado
) {
}
