package com.farmape.ms.inventario.api.dto;

import java.util.List;

public record ResumenInventarioResponse(
        long totalProductosActivos,
        long productosConStockBajo,
        long lotesPorVencer,
        long entradasHoy,
        long salidasHoy,
        long ajustesHoy,
        List<LoteProductoResponse> proximosVencimientos,
        List<MovimientoAlmacenResponse> ultimosMovimientos
) {
}
