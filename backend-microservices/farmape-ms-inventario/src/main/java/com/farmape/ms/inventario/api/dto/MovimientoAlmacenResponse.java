package com.farmape.ms.inventario.api.dto;

import java.time.LocalDateTime;

public record MovimientoAlmacenResponse(
        Integer idMovimiento,
        Integer idProducto,
        String producto,
        Integer idLote,
        String numeroLote,
        Integer idTrabajador,
        String tipoMovimiento,
        String motivo,
        Integer cantidad,
        String referenciaTipo,
        Integer referenciaId,
        LocalDateTime fechaMovimiento,
        String observacion
) {
}
