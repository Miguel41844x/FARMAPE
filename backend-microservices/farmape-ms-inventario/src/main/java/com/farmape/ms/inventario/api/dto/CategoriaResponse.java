package com.farmape.ms.inventario.api.dto;

public record CategoriaResponse(
        Integer idCategoria,
        String nombre,
        String descripcion,
        Boolean activo
) {
}
