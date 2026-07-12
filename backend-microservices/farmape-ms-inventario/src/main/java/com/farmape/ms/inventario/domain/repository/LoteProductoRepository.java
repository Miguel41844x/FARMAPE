package com.farmape.ms.inventario.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.farmape.ms.inventario.domain.model.LoteProducto;
import com.farmape.ms.inventario.domain.model.Producto;

import jakarta.persistence.LockModeType;

public interface LoteProductoRepository extends JpaRepository<LoteProducto, Integer> {

    Optional<LoteProducto> findByProductoAndNumeroLote(Producto producto, String numeroLote);

    List<LoteProducto> findByProductoAndStockDisponibleGreaterThanOrderByFechaVencimientoAsc(
            Producto producto,
            Integer stockMinimo
    );

    long countByFechaVencimientoBetweenAndStockDisponibleGreaterThan(
            LocalDate desde,
            LocalDate hasta,
            Integer stockMinimo
    );

    List<LoteProducto> findTop5ByStockDisponibleGreaterThanOrderByFechaVencimientoAsc(Integer stockMinimo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM LoteProducto l WHERE l.idLote = :idLote")
    Optional<LoteProducto> findByIdForUpdate(@Param("idLote") Integer idLote);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT l
            FROM LoteProducto l
            WHERE l.producto = :producto
              AND l.stockDisponible > 0
              AND l.estado = 'Disponible'
            ORDER BY l.fechaVencimiento ASC, l.fechaIngreso ASC, l.idLote ASC
            """)
    List<LoteProducto> findDisponiblesForUpdateByProductoFefo(@Param("producto") Producto producto);
}
