package com.farmape.backend.reportes.dto;

import java.time.LocalDate;

public record StockCriticoResponse(
        Integer idProducto,
        String producto,
        Integer stockActual,
        Integer stockMinimo,
        LocalDate fechaVencimiento
) {
}
