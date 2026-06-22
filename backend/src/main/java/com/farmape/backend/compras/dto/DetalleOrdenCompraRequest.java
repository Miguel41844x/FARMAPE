package com.farmape.backend.compras.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DetalleOrdenCompraRequest(
        @NotNull Integer idProducto,
        @NotNull @Min(1) Integer cantidad,
        @NotNull @DecimalMin("0.00") BigDecimal precioUnitario
) {
}
