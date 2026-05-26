package com.farmape.backend.caja.dto;

import com.farmape.backend.caja.enums.MetodoPago;
import com.farmape.backend.caja.enums.TipoComprobante;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RegistrarPagoRequest(
        @NotNull(message = "El id del cajero es obligatorio")
        Integer idCajero,

        @NotNull(message = "El monto pagado es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto pagado debe ser mayor a 0")
        BigDecimal montoPagado,

        @NotNull(message = "El método de pago es obligatorio")
        MetodoPago metodoPago,

        @NotNull(message = "El tipo de comprobante es obligatorio")
        TipoComprobante tipoComprobante
) {
}