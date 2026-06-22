package com.farmape.backend.reportes.dto;

import java.time.LocalDateTime;

public record InformeResponse(
        Integer idInforme,
        String area,
        String titulo,
        String descripcion,
        String trabajador,
        LocalDateTime fechaEmision
) {
}
