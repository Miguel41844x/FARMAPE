package com.farmape.ms.ventas.api.dto;

import java.util.List;

import com.farmape.ms.ventas.domain.model.CanalPedido;

public record CrearVentaRequest(
        Integer idCliente,
        String cliente,
        String nombreCliente,
        Integer idEmpleado,
        String empleado,
        CanalPedido canalPedido,
        String observacion,
        List<DetalleVentaRequest> detalles,
        List<DetalleVentaRequest> productos
) {
}
