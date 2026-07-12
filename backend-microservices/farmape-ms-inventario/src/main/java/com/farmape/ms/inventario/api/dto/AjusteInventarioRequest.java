package com.farmape.ms.inventario.api.dto;

public record AjusteInventarioRequest(
        Integer idProducto,
        Integer idLote,
        Integer idTrabajador,
        Integer stockFisico,
        String referenciaTipo,
        Integer referenciaId,
        String observacion
) {
}
