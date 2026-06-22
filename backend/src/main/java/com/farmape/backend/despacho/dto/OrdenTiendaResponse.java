package com.farmape.backend.despacho.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrdenTiendaResponse(
        Integer idOrdenVenta,
        String cliente,
        LocalDateTime fechaOrden,
        BigDecimal total,
        String estado
) {
}
