package com.farmape.ms.inventario.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductoRequest(
        Integer idCategoria,
        String nombre,
        String descripcion,
        String laboratorio,
        BigDecimal precioCompra,
        BigDecimal precioVenta,
        Integer stockActual,
        Integer stockMinimo,
        LocalDate fechaVencimiento,
        String estado
) {
}
