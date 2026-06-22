package com.farmape.backend.auth.dto;

import java.time.LocalDateTime;

public record SolicitarRestablecimientoResponse(
        boolean success,
        Long idSolicitud,
        String estado,
        LocalDateTime fechaSolicitud,
        String mensaje
) {
}
