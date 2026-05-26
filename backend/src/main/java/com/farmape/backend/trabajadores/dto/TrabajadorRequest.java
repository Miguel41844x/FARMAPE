package com.farmape.backend.trabajadores.dto;

import com.farmape.backend.trabajadores.enums.EstadoTrabajador;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TrabajadorRequest(

        @NotNull(message = "El rol es obligatorio")
        Integer idRol,

        @NotBlank(message = "El DNI es obligatorio")
        @Size(max = 20, message = "El DNI no debe superar los 20 caracteres")
        String dni,

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 100, message = "Los nombres no deben superar los 100 caracteres")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100, message = "Los apellidos no deben superar los 100 caracteres")
        String apellidos,

        @Size(max = 20, message = "El teléfono no debe superar los 20 caracteres")
        String telefono,

        @Size(max = 150, message = "La dirección no debe superar los 150 caracteres")
        String direccion,

        EstadoTrabajador estado
) {
}