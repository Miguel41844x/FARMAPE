package com.farmape.ms.inventario.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoteProductoResponse(
        Integer idLote,
        Integer idProducto,
        String producto,
        String numeroLote,
        LocalDate fechaVencimiento,
        BigDecimal costoUnitario,
        Integer stockDisponible,
        String estado,
        LocalDateTime fechaIngreso
) {
}
