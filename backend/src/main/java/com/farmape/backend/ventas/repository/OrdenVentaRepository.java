package com.farmape.backend.ventas.repository;

import com.farmape.backend.ventas.enums.EstadoOrdenVenta;
import com.farmape.backend.ventas.model.OrdenVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenVentaRepository extends JpaRepository<OrdenVenta, Integer> {

    List<OrdenVenta> findByEstado(EstadoOrdenVenta estado);
    List<OrdenVenta> findTop4ByOrderByIdOrdenVentaDesc();
}