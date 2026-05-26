package com.farmape.backend.clientes.dto;

import com.farmape.backend.clientes.enums.TipoCliente;

public record ClienteResponse(
        Integer idCliente,
        String dniRuc,
        String nombres,
        String apellidos,
        String telefono,
        String whatsapp,
        String direccion,
        String email,
        TipoCliente tipoCliente
) {
}