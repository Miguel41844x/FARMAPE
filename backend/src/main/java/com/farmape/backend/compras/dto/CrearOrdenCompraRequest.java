package com.farmape.backend.compras.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CrearOrdenCompraRequest(
        @NotNull Integer idProveedor,
        LocalDate fechaEntrega,
        String medioPedido,
        String observaciones,
        String observacion,
        @NotEmpty List<@Valid DetalleOrdenCompraRequest> detalles
) {
}
