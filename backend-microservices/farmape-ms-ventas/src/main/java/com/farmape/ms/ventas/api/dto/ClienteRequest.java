package com.farmape.ms.ventas.api.dto;

public record ClienteRequest(
        String tipoCliente,
        String dniRuc,
        String documento,
        String nombres,
        String apellidos,
        String telefono,
        String whatsapp,
        String direccion,
        String email
) {
}
