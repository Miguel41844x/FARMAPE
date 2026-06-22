package com.farmape.backend.reportes.dto;

import java.time.LocalDateTime;

public record AccionGerenciaResponse(
        Integer idAccion,
        Integer idInforme,
        String informe,
        String area,
        String gerente,
        String accionTomar,
        LocalDateTime fechaRegistro,
        String estado
) {
}
