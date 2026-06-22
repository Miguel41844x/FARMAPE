package com.farmape.backend.almacen.repository;

import com.farmape.backend.almacen.model.LoteProducto;
import com.farmape.backend.productos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoteProductoRepository extends JpaRepository<LoteProducto, Integer> {

    Optional<LoteProducto> findByProductoAndNumeroLote(Producto producto, String numeroLote);

    long countByFechaVencimientoBetweenAndStockDisponibleGreaterThan(LocalDate desde, LocalDate hasta, Integer stockMinimo);

    List<LoteProducto> findTop5ByStockDisponibleGreaterThanOrderByFechaVencimientoAsc(Integer stockMinimo);
}
