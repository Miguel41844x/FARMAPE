package com.farmape.backend.despacho.dto;

public record RepartoDomicilioResponse(
        Integer idReparto,
        Integer idOrdenVenta,
        String cliente,
        String direccion,
        String repartidor,
        String estado
) {
}
