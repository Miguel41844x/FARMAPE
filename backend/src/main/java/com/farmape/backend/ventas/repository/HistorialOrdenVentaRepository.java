package com.farmape.backend.ventas.repository;

import com.farmape.backend.ventas.model.HistorialOrdenVenta;
import com.farmape.backend.ventas.model.OrdenVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialOrdenVentaRepository extends JpaRepository<HistorialOrdenVenta, Long> {
    List<HistorialOrdenVenta> findByOrdenVentaOrderByFechaCambioAsc(OrdenVenta ordenVenta);
}
