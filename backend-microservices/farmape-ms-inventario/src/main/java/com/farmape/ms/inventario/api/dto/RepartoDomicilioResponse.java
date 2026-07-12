package com.farmape.ms.inventario.api.dto;

public record RepartoDomicilioResponse(
        Integer idReparto,
        Integer idOrdenVenta,
        String cliente,
        String direccion,
        String repartidor,
        String estado
) {
}
