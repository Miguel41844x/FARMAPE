package com.farmape.backend.auditoria.dto;

import java.util.List;

public record AuditoriaResumenResponse(
        long totalEventos,
        long eventosHoy,
        long eventosVentas,
        long eventosCompras,
        long eventosInventario,
        long eventosUsuariosRoles,
        long eventosRiesgo,
        List<ModuloAuditoriaResumen> modulos,
        List<AccionAuditoriaResumen> acciones,
        List<AuditoriaEventoResponse> recientes
) {
    public record ModuloAuditoriaResumen(String modulo, long total) {}
    public record AccionAuditoriaResumen(String accion, long total) {}
}
