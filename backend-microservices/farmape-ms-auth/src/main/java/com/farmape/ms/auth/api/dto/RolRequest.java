package com.farmape.ms.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record RolRequest(
        @NotBlank @Size(max = 50) String codigo,
        @NotBlank @Size(max = 80) String nombreRol,
        @Size(max = 255) String descripcion,
        Set<Integer> idPermisos
) {}