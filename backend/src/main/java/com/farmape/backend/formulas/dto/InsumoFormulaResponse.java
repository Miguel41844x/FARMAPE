package com.farmape.backend.formulas.dto;

import java.math.BigDecimal;

public record InsumoFormulaResponse(
        Integer idProducto,
        String nombre,
        String descripcion,
        String laboratorio,
        BigDecimal precioCompra,
        BigDecimal precioVenta,
        Integer stockActual,
        String estado
) {
}
