package com.farmape.backend.formulas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record PresupuestarFormulaRequest(
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

        @NotNull(message = "El presupuesto es obligatorio")
        @DecimalMin(value = "0.01", message = "El presupuesto debe ser mayor a 0")
        BigDecimal presupuesto,

        String descripcionFormula,

        String instruccionesUso,

        List<@Valid ComponenteRecetaRequest> componentes,

        @NotEmpty(message = "Debe registrar al menos un insumo usado en la fórmula")
        List<@Valid InsumoFormulaRequest> insumos
) {
}
