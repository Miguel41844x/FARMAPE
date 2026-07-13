package com.farmape.ms.ventas.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.farmape.ms.ventas.domain.model.CanalPedido;
import com.farmape.ms.ventas.domain.model.EstadoVenta;

public record VentaResponse(
        Integer idOrdenVenta,
        Integer idCliente,
        String cliente,
        Integer idEmpleado,
        String empleado,
        CanalPedido canalPedido,
        EstadoVenta estado,
        LocalDateTime fechaOrden,
        BigDecimal total,
        String observacion,
        List<DetalleVentaResponse> detalles
) {
}
