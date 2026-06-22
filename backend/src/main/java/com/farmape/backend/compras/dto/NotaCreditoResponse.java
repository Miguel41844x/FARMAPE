package com.farmape.backend.compras.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NotaCreditoResponse(
        Integer idNotaCredito,
        Integer idNotaCreditoProveedor,
        Integer idFacturaProveedor,
        String numero,
        String codigo,
        FacturaProveedorResponse factura,
        String motivo,
        String descripcion,
        LocalDate fechaEmision,
        BigDecimal monto
) {
}
