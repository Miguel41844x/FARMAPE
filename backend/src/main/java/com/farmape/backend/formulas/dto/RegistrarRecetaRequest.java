package com.farmape.backend.formulas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record RegistrarRecetaRequest(
        @NotBlank(message = "El DNI del paciente es obligatorio")
        String dniPaciente,

        String nombrePaciente,

        @NotBlank(message = "El médico prescriptor es obligatorio")
        String medicoPrescriptor,

        String numeroColegiatura,

        @NotBlank(message = "El diagnóstico es obligatorio")
        String diagnostico,

        String contraindicaciones,

        @NotBlank(message = "El tipo de fórmula es obligatorio")
        String tipoFormula,

        List<@Valid ComponenteRecetaRequest> componentes
) {
}
