package com.farmape.backend.formulas.dto;

import java.math.BigDecimal;

public record PresupuestoFormulaResponse(
        boolean success,
        Integer idReceta,
        Integer idFormula,
        Integer idOrdenVenta,
        BigDecimal presupuesto,
        String estado,
        String mensaje
) {
}
