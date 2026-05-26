package com.farmape.backend.ventas.repository;

import com.farmape.backend.ventas.model.DetalleOrdenVenta;
import com.farmape.backend.ventas.model.OrdenVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleOrdenVentaRepository extends JpaRepository<DetalleOrdenVenta, Integer> {

    List<DetalleOrdenVenta> findByOrdenVenta(OrdenVenta ordenVenta);
}