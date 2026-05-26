package com.farmape.backend.productos.dto;

import com.farmape.backend.productos.enums.EstadoProducto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductoResponse(
        Integer idProducto,
        Integer idCategoria,
        String categoria,
        String nombre,
        String descripcion,
        String laboratorio,
        BigDecimal precioCompra,
        BigDecimal precioVenta,
        Integer stockActual,
        Integer stockMinimo,
        LocalDate fechaVencimiento,
        EstadoProducto estado
) {
}