package com.farmape.backend.compras.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RegistrarNotaCreditoRequest(
        @NotNull Integer idFacturaProveedor,
        @NotBlank String motivo,
        String descripcion,
        @NotNull @DecimalMin("0.01") BigDecimal monto
) {
}
