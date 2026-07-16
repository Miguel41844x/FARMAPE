package com.farmape.ms.auth.api.dto;

public record PermisoResponse(
        Integer idPermiso,
        String codigo,
        String nombre,
        String modulo,
        Boolean activo
) {}