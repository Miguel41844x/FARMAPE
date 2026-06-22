package com.farmape.backend.compras.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistrarFacturaProveedorRequest(
        @NotNull Integer idOrdenCompra,
        String serie,
        @NotBlank String numero,
        @NotNull LocalDate fechaEmision,
        LocalDate fechaVencimiento,
        @NotNull @DecimalMin("0.00") BigDecimal total,
        String condicionPago,
        String tipoPago,
        Short diasCredito
) {
}
