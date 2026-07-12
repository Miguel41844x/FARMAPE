package com.farmape.ms.inventario.api.dto;

import java.time.LocalDate;

public record IngresoAlmacenResponse(
        Integer idIngreso,
        Integer idProducto,
        String producto,
        Integer cantidad,
        String lote,
        LocalDate fechaVencimiento,
        Integer idProveedor,
        String proveedor,
        String estado
) {
}
