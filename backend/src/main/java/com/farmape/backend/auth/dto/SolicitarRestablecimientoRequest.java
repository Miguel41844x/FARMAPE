package com.farmape.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SolicitarRestablecimientoRequest(
        @NotBlank(message = "Ingrese su usuario o correo")
        @Size(max = 100, message = "El usuario o correo no debe superar 100 caracteres")
        String usuarioOCorreo,

        @Size(max = 300, message = "El mensaje no debe superar 300 caracteres")
        String mensaje
) {
}
