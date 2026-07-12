package com.farmape.ms.inventario.api.dto;

public record InformeAlmacenResponse(
        Integer idIndicador,
        String indicador,
        Long cantidad,
        String detalle
) {
}
