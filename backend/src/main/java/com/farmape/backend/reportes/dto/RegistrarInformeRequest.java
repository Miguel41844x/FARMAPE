package com.farmape.backend.reportes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrarInformeRequest(
        @NotBlank(message = "El área es obligatoria")
        @Size(max = 50, message = "El área no debe superar 50 caracteres")
        String area,

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 150, message = "El título no debe superar 150 caracteres")
        String titulo,

        @Size(max = 1000, message = "La descripción no debe superar 1000 caracteres")
        String descripcion
) {
}
