package com.farmape.backend.compras.dto;

public record ProveedorResponse(
        Integer idProveedor,
        String ruc,
        String razonSocial,
        String telefono,
        String email,
        String direccion,
        String tipoProveedor,
        Boolean activo
) {
}
