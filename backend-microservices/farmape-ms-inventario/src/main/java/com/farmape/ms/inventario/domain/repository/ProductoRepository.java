package com.farmape.ms.inventario.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.farmape.ms.inventario.domain.model.EstadoProducto;
import com.farmape.ms.inventario.domain.model.Producto;

import jakarta.persistence.LockModeType;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    long countByEstado(EstadoProducto estado);

    List<Producto> findAllByOrderByNombreAsc();

    List<Producto> findByEstadoOrderByNombreAsc(EstadoProducto estado);

    List<Producto> findByNombreContainingIgnoreCaseOrderByNombreAsc(String nombre);

    Optional<Producto> findBySku(String sku);

    @Query("""
            SELECT p
            FROM Producto p
            WHERE p.stockActual <= p.stockMinimo
            ORDER BY p.stockActual ASC, p.nombre ASC
            """)
    List<Producto> findProductosConStockBajo();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.idProducto = :idProducto")
    Optional<Producto> findByIdForUpdate(@Param("idProducto") Integer idProducto);
}
