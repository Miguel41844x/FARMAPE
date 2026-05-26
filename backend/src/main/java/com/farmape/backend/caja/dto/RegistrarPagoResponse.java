package com.farmape.backend.caja.dto;

import com.farmape.backend.ventas.dto.OrdenVentaResponse;

public record RegistrarPagoResponse(
        OrdenVentaResponse ordenVenta,
        PagoVentaResponse pago,
        ComprobanteVentaResponse comprobante
) {
}