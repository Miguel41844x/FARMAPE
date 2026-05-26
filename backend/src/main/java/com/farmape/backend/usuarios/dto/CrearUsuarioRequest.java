package com.farmape.backend.usuarios.dto;

import com.farmape.backend.usuarios.enums.EstadoCuentaUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CrearUsuarioRequest(
        @NotBlank(message = "El DNI es obligatorio")
        String dni,

        @NotBlank(message = "Los nombres son obligatorios")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        String apellidos,

        String telefono,

        String direccion,

        @NotBlank(message = "El usuario es obligatorio")
        String usuario,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String email,

        @NotBlank(message = "La clave es obligatoria")
        String clave,

        @NotBlank(message = "El rol es obligatorio")
        String rol,

        EstadoCuentaUsuario estado
) {
}