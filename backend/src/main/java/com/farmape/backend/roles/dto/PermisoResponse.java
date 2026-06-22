package com.farmape.backend.roles.dto;

public record PermisoResponse(
        Integer idPermiso,
        String codigo,
        String nombre,
        String modulo,
        Boolean activo
) {
}
