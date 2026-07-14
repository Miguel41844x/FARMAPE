package com.farmape.ms.auth.auth.dto;

import java.time.LocalDateTime;

public record SolicitarRestablecimientoResponse(
        boolean success,
        Long idSolicitud,
        String estado,
        LocalDateTime fechaSolicitud,
        String mensaje
) {
}
