package com.farmape.backend.productos.dto;

import com.farmape.backend.productos.enums.EstadoProducto;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoProductoRequest(
        @NotNull(message = "El estado es obligatorio") EstadoProducto estado
) {
}
