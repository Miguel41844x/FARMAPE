package com.farmape.backend.perfil.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActualizarPerfilRequest(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        @Size(max = 100, message = "El email no debe superar los 100 caracteres")
        String email,

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 100, message = "Los nombres no deben superar los 100 caracteres")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100, message = "Los apellidos no deben superar los 100 caracteres")
        String apellidos,

        @Size(max = 20, message = "El teléfono no debe superar los 20 caracteres")
        String telefono,

        @Size(max = 150, message = "La dirección no debe superar los 150 caracteres")
        String direccion
) {}
