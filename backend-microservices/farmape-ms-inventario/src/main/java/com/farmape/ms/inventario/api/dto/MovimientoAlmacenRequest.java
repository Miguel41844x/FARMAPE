package com.farmape.ms.inventario.api.dto;

public record MovimientoAlmacenRequest(
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
