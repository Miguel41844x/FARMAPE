package com.farmape.backend.almacen.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RegistrarIngresoAlmacenRequest(
        Integer referenciaId,
        @NotNull Integer idProducto,
        @NotNull @Min(1) Integer cantidad,
        String lote,
        LocalDate fechaVencimiento,
        Integer idProveedor
) {
}
