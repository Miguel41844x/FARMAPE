package com.farmape.backend.auditoria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrarAuditoriaRequest(
        @NotBlank @Size(max = 50) String modulo,
        @NotBlank @Size(max = 80) String entidad,
        @Size(max = 80) String entidadId,
        @NotBlank @Size(max = 40) String accion,
        @NotBlank String descripcion,
        String valorAnterior,
        String valorNuevo,
        @Size(max = 20) String severidad
) {
}
