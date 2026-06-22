package com.farmape.backend.almacen.repository;

import com.farmape.backend.almacen.model.DetalleRecepcionCompra;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleRecepcionCompraRepository extends JpaRepository<DetalleRecepcionCompra, Integer> {

    @Override
    @EntityGraph(attributePaths = {
            "recepcion",
            "recepcion.ordenCompra",
            "recepcion.ordenCompra.proveedor",
            "producto",
            "lote"
    })
    List<DetalleRecepcionCompra> findAll();
}
