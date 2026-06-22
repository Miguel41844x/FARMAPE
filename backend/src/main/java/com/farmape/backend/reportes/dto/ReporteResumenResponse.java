package com.farmape.backend.reportes.dto;

import java.util.List;

public record ReporteResumenResponse(
        List<KpiResponse> kpis,
        List<SerieItemResponse> ordenesPorEstado,
        List<SerieItemResponse> ventasPorCanal,
        List<SerieItemResponse> pagosPorMetodo,
        List<SerieItemResponse> ventasUltimosDias,
        List<SerieItemResponse> accionesPorEstado,
        List<StockCriticoResponse> stockCritico
) {
}
