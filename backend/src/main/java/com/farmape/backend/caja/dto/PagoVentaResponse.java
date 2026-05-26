package com.farmape.backend.caja.dto;

import com.farmape.backend.caja.enums.EstadoPagoVenta;
import com.farmape.backend.caja.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoVentaResponse(
        Integer idPagoVenta,
        Integer idOrdenVenta,
        Integer idCajero,
        String cajero,
        LocalDateTime fechaPago,
        BigDecimal montoPagado,
        MetodoPago metodoPago,
        EstadoPagoVenta estado
) {
}