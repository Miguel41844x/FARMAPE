package com.farmape.backend.ventas.dto;

import com.farmape.backend.ventas.enums.CanalPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CrearOrdenVentaRequest(

        @NotNull(message = "El cliente es obligatorio")
        Integer idCliente,

        @NotNull(message = "El canal de pedido es obligatorio")
        CanalPedido canalPedido,

        String observacion,

        @NotEmpty(message = "La venta debe tener al menos un producto")
        List<@Valid DetalleVentaRequest> detalles
) {
}
