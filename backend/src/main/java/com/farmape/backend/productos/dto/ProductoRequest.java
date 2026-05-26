package com.farmape.backend.productos.dto;

import com.farmape.backend.productos.enums.EstadoProducto;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductoRequest(

        @NotNull(message = "La categoría es obligatoria")
        Integer idCategoria,

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        String descripcion,

        String laboratorio,

        @DecimalMin(value = "0.00", message = "El precio de compra no puede ser negativo")
        BigDecimal precioCompra,

        @NotNull(message = "El precio de venta es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio de venta debe ser mayor a 0")
        BigDecimal precioVenta,

        @Min(value = 0, message = "El stock actual no puede ser negativo")
        Integer stockActual,

        @Min(value = 0, message = "El stock mínimo no puede ser negativo")
        Integer stockMinimo,

        LocalDate fechaVencimiento,

        EstadoProducto estado
) {
}