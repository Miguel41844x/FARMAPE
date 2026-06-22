package com.farmape.backend.despacho.repository;

import com.farmape.backend.despacho.model.Despacho;
import com.farmape.backend.ventas.model.OrdenVenta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DespachoRepository extends JpaRepository<Despacho, Integer> {

    Optional<Despacho> findByOrdenVenta(OrdenVenta ordenVenta);

    @Override
    @EntityGraph(attributePaths = {"ordenVenta", "ordenVenta.cliente", "encargadoDespacho", "repartidor"})
    List<Despacho> findAll();
}
