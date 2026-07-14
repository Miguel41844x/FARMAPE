package com.farmape.ms.auth.roles.dto;

import jakarta.validation.constraints.NotNull;

public record EstadoRolRequest(@NotNull Boolean activo) {}