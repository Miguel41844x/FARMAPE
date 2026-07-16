package com.farmape.ms.auth.api.dto;

import jakarta.validation.constraints.NotNull;

public record EstadoRolRequest(@NotNull Boolean activo) {}