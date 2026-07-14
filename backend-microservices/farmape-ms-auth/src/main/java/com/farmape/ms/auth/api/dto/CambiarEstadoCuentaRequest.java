package com.farmape.ms.auth.usuarios.dto;

import com.farmape.ms.auth.usuarios.enums.EstadoCuentaUsuario;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoCuentaRequest(
        @NotNull EstadoCuentaUsuario estado
) {
}