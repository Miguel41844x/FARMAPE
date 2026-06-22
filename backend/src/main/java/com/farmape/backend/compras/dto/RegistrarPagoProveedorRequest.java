package com.farmape.backend.compras.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistrarPagoProveedorRequest(
        @NotNull Integer idFacturaProveedor,
        LocalDate fechaPago,
        @NotNull @DecimalMin("0.01") BigDecimal monto,
        String metodoPago,
        String referencia,
        String observacion
) {
}
