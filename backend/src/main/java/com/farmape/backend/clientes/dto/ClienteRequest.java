package com.farmape.backend.clientes.dto;

import com.farmape.backend.clientes.enums.TipoCliente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ClienteRequest(

        @NotBlank(message = "El DNI o RUC es obligatorio")
        @Size(max = 20)
        String dniRuc,

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 100)
        String nombres,

        @Size(max = 100)
        String apellidos,

        @Size(max = 20)
        String telefono,

        @Size(max = 20)
        String whatsapp,

        @Size(max = 150)
        String direccion,

        @Size(max = 100)
        String email,

        @NotNull(message = "El tipo de cliente es obligatorio")
        TipoCliente tipoCliente
) {
}