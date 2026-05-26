package com.farmape.backend.usuarios.dto;

import com.farmape.backend.usuarios.enums.EstadoCuentaUsuario;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoCuentaRequest(
        @NotNull EstadoCuentaUsuario estado
) {
}