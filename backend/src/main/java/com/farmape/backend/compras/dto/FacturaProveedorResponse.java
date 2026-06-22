package com.farmape.backend.compras.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FacturaProveedorResponse(
        Integer idFacturaProveedor,
        Integer idFacturaCompra,
        Integer idOrdenCompra,
        String serie,
        String numero,
        String numeroFactura,
        ProveedorResponse proveedor,
        String razonSocialProveedor,
        LocalDate fechaEmision,
        LocalDateTime fechaRegistro,
        LocalDate fechaVencimiento,
        String condicionPago,
        String tipoPago,
        BigDecimal subtotal,
        BigDecimal igv,
        BigDecimal total,
        BigDecimal montoTotal,
        String estado
) {
}
