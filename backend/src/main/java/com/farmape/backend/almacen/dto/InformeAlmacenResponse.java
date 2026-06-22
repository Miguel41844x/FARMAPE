package com.farmape.backend.almacen.dto;

public record InformeAlmacenResponse(
        Integer idIndicador,
        String indicador,
        Long cantidad,
        String detalle
) {
}
