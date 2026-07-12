package com.farmape.ms.inventario.api.dto;

public record VerificacionProductoResponse(
        Integer idVerificacion,
        Integer idPedidoCompra,
        Integer idProducto,
        String producto,
        Integer cantidadPedida,
        Integer cantidadRecibida,
        String estado,
        String observacion
) {
}
