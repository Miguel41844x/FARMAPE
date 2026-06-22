package com.farmape.backend.roles.dto;

import jakarta.validation.constraints.NotNull;

public record EstadoRolRequest(@NotNull Boolean activo) {
}
