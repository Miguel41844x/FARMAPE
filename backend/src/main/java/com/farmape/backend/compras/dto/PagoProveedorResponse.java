package com.farmape.backend.compras.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoProveedorResponse(
        Integer idPagoProveedor,
        Integer idFacturaProveedor,
        FacturaProveedorResponse factura,
        LocalDateTime fechaPago,
        String metodoPago,
        String referencia,
        BigDecimal monto,
        BigDecimal montoPagado,
        String observacion
) {
}
