package com.farmape.backend.compras.dto;

import jakarta.validation.constraints.NotBlank;

public record ProveedorRequest(
        @NotBlank String ruc,
        @NotBlank String razonSocial,
        String telefono,
        String email,
        String direccion,
        String tipoProveedor,
        Boolean activo
) {
}
