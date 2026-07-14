package com.farmape.ms.auth.usuarios.dto;

import com.farmape.ms.auth.usuarios.enums.EstadoCuentaUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ActualizarUsuarioRequest(
        @NotBlank(message = "El DNI es obligatorio")
        @Size(max = 20, message = "El DNI no debe superar 20 caracteres")
        String dni,

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 100, message = "Los nombres no deben superar 100 caracteres")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100, message = "Los apellidos no deben superar 100 caracteres")
        String apellidos,

        @Size(max = 20, message = "El teléfono no debe superar 20 caracteres")
        String telefono,

        @Size(max = 150, message = "La dirección no debe superar 150 caracteres")
        String direccion,

        @NotBlank(message = "El usuario es obligatorio")
        @Size(max = 50, message = "El usuario no debe superar 50 caracteres")
        String usuario,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Ingrese un email válido")
        @Size(max = 100, message = "El email no debe superar 100 caracteres")
        String email,

        @NotNull(message = "El rol es obligatorio")
        Integer idRol,

        EstadoCuentaUsuario estado,

        @Size(min = 6, max = 100, message = "La nueva contraseña debe tener entre 6 y 100 caracteres")
        String nuevaClave
) {
}
