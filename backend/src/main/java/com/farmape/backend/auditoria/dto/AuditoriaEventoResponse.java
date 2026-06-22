package com.farmape.backend.auditoria.dto;

import java.time.LocalDateTime;

public record AuditoriaEventoResponse(
        Long idAuditoria,
        LocalDateTime fechaEvento,
        String modulo,
        String entidad,
        String entidadId,
        String accion,
        String descripcion,
        String valorAnterior,
        String valorNuevo,
        Integer idUsuario,
        Integer idTrabajador,
        String usuario,
        String severidad,
        String origen,
        String tipoEvento,
        Boolean editable,
        String ip
) {
}
