package com.farmape.ms.inventario.api.dto;

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
