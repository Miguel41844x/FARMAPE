package com.farmape.backend.ventas.dto;

import com.farmape.backend.ventas.enums.CanalPedido;
import com.farmape.backend.ventas.enums.EstadoOrdenVenta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrdenVentaResponse(
        Integer idOrdenVenta,
        Integer idCliente,
        String cliente,
        Integer idEmpleado,
        String empleado,
        CanalPedido canalPedido,
        EstadoOrdenVenta estado,
        LocalDateTime fechaOrden,
        BigDecimal total,
        String observacion,
        List<DetalleVentaResponse> detalles
) {
}