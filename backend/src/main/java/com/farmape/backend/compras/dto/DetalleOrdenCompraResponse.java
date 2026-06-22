package com.farmape.backend.compras.dto;

import java.math.BigDecimal;

public record DetalleOrdenCompraResponse(
        Integer idDetalleCompra,
        Integer idProducto,
        String producto,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}
