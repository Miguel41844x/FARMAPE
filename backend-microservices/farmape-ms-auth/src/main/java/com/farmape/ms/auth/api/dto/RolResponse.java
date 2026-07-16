package com.farmape.ms.auth.api.dto;

import java.util.List;

public record RolResponse(
        Integer idRol,
        String codigo,
        String nombreRol,
        String descripcion,
        Boolean activo,
        List<Integer> idPermisos,
        List<String> permisos
) {}