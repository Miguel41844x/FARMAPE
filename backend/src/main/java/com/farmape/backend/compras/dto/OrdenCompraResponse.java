package com.farmape.backend.compras.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record OrdenCompraResponse(
        Integer idOrdenCompra,
        String codigo,
        String numeroOrden,
        ProveedorResponse proveedor,
        String razonSocialProveedor,
        LocalDateTime fechaPedido,
        LocalDateTime fechaCreacion,
        LocalDate fechaEntrega,
        String medioPedido,
        String estado,
        BigDecimal total,
        String observacion,
        List<DetalleOrdenCompraResponse> detalles
) {
}
