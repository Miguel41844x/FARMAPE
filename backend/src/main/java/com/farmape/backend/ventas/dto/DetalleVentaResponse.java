package com.farmape.backend.ventas.dto;

import java.math.BigDecimal;

public record DetalleVentaResponse(
        Integer idDetalleVenta,
        Integer idProducto,
        String producto,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}