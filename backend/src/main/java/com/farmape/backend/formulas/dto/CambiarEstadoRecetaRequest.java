package com.farmape.backend.formulas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambiarEstadoRecetaRequest(
        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 30, message = "El estado no debe superar 30 caracteres")
        String estado,

        @Size(max = 300, message = "La observación no debe superar 300 caracteres")
        String observacion
) {
}
