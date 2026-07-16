package com.farmape.ms.auth.api.dto;

import com.farmape.ms.auth.domain.model.EstadoCuentaUsuario;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoCuentaRequest(
        @NotNull EstadoCuentaUsuario estado
) {
}