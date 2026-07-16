package com.farmape.ms.auth.api.dto;

import java.time.LocalDateTime;

public record SolicitudRestablecimientoResponse(
        Long idSolicitud,
        String usuarioOCorreo,
        Integer idCuenta,
        String usuarioEncontrado,
        String emailEncontrado,
        String mensaje,
        String estado,
        LocalDateTime fechaSolicitud
) {
}
