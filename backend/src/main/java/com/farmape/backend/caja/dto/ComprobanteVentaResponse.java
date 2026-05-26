package com.farmape.backend.caja.dto;

import com.farmape.backend.caja.enums.TipoComprobante;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ComprobanteVentaResponse(
        Integer idComprobante,
        Integer idOrdenVenta,
        TipoComprobante tipoComprobante,
        String serie,
        String numero,
        LocalDateTime fechaEmision,
        BigDecimal montoTotal
) {
}