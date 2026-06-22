package com.farmape.backend.reportes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrarAccionRequest(
        @NotNull(message = "El informe es obligatorio")
        Integer idInforme,

        @NotBlank(message = "La acción a tomar es obligatoria")
        @Size(max = 1000, message = "La acción no debe superar 1000 caracteres")
        String accionTomar
) {
}
