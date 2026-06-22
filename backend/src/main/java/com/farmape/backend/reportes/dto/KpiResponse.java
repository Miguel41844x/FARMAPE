package com.farmape.backend.reportes.dto;

import java.math.BigDecimal;

public record KpiResponse(
        String codigo,
        String titulo,
        BigDecimal valor,
        String formato,
        String descripcion
) {
}
