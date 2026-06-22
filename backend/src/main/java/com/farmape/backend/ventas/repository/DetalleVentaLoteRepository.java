package com.farmape.backend.ventas.repository;

import com.farmape.backend.ventas.model.DetalleOrdenVenta;
import com.farmape.backend.ventas.model.DetalleVentaLote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleVentaLoteRepository extends JpaRepository<DetalleVentaLote, Integer> {

    List<DetalleVentaLote> findByDetalleVenta(DetalleOrdenVenta detalleVenta);
}
