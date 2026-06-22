package com.farmape.backend.formulas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ComponenteRecetaRequest(
        @NotBlank(message = "El nombre del insumo es obligatorio")
        String nombre_insumo,

        @NotNull(message = "La cantidad usada es obligatoria")
        @DecimalMin(value = "0.001", message = "La cantidad usada debe ser mayor a 0")
        BigDecimal cantidad_usada,

        @NotBlank(message = "La unidad de medida es obligatoria")
        String unidad_medida
) {
}
