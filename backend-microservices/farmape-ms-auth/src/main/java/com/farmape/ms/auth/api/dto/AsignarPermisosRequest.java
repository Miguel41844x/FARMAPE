package com.farmape.ms.auth.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record AsignarPermisosRequest(@NotNull Set<Integer> idPermisos) {}