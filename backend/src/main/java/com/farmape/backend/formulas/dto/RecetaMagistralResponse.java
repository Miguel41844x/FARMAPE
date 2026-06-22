package com.farmape.backend.formulas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RecetaMagistralResponse(
        Integer idReceta,
        Integer idCliente,
        String dniPaciente,
        String nombrePaciente,
        Integer idQuimicoFarmaceutico,
        String quimicoFarmaceutico,
        String medicoPrescriptor,
        String numeroColegiatura,
        String descripcionReceta,
        String contraindicaciones,
        BigDecimal presupuesto,
        LocalDateTime fechaReceta,
        String estado,
        Integer idOrdenVenta,
        Integer idFormula,
        String estadoFormula
) {
}
