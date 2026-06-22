package com.farmape.backend.reportes.dto;

import java.math.BigDecimal;

public record SerieItemResponse(
        String etiqueta,
        BigDecimal valor
) {
}
