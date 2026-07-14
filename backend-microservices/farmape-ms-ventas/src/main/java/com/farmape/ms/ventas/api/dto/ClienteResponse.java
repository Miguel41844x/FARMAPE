package com.farmape.ms.ventas.api.dto;

import java.time.LocalDateTime;

public record ClienteResponse(
        Integer idCliente,
        String tipoCliente,
        String dniRuc,
        String documento,
        String nombres,
        String apellidos,
        String telefono,
        String whatsapp,
        String direccion,
        String email,
        LocalDateTime fechaRegistro
) {
}
