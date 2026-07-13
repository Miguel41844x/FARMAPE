package com.farmape.ms.ventas.application.client;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InventarioProductoResponse(
        Integer idProducto,
        Integer idCategoria,
        String categoria,
        String sku,
        String nombre,
        String descripcion,
        String laboratorio,
        BigDecimal precioCompra,
        BigDecimal precioVenta,
        Integer stockActual,
        Integer stockMinimo,
        LocalDate fechaVencimiento,
        Boolean requiereReceta,
        String estado
) {
}
