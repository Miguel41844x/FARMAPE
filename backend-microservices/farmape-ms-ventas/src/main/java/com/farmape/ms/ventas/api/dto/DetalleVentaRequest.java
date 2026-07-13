package com.farmape.ms.ventas.api.dto;

public record DetalleVentaRequest(
        Integer idProducto,
        Integer cantidad
) {
}
