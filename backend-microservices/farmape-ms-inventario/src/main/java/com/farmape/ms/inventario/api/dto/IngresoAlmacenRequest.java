package com.farmape.ms.inventario.api.dto;

import java.time.LocalDate;

public record IngresoAlmacenRequest(
        Integer referenciaId,
        Integer idProducto,
        Integer cantidad,
        String lote,
        LocalDate fechaVencimiento,
        Integer idProveedor
) {
}
