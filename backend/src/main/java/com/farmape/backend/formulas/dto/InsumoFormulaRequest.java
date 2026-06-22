package com.farmape.backend.formulas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record InsumoFormulaRequest(
        @NotNull(message = "El producto/insumo es obligatorio")
        Integer idProducto,

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.001", message = "La cantidad debe ser mayor a 0")
        BigDecimal cantidad,

        String unidadMedida
) {
}
