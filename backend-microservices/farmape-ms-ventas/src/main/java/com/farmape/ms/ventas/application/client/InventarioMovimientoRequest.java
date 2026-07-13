package com.farmape.ms.ventas.application.client;

public record InventarioMovimientoRequest(
        Integer idProducto,
        Integer idLote,
        Integer idTrabajador,
        String tipoMovimiento,
        String motivo,
        Integer cantidad,
        String referenciaTipo,
        Integer referenciaId,
        String observacion
) {
}
