package com.farmape.ms.inventario.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmape.ms.inventario.domain.model.MovimientoAlmacen;
import com.farmape.ms.inventario.domain.model.TipoMovimiento;

public interface MovimientoAlmacenRepository extends JpaRepository<MovimientoAlmacen, Integer> {

    boolean existsByReferenciaTipoAndReferenciaId(String referenciaTipo, Integer referenciaId);

    List<MovimientoAlmacen> findTop20ByOrderByFechaMovimientoDesc();

    long countByTipoMovimientoAndFechaMovimientoBetween(
            TipoMovimiento tipoMovimiento,
            LocalDateTime desde,
            LocalDateTime hasta
    );

    List<MovimientoAlmacen> findTop20ByProductoIdProductoOrderByFechaMovimientoDesc(Integer idProducto);
}
