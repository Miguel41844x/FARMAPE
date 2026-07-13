package com.farmape.ms.ventas.api.dto;

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
