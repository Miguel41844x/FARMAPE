package com.farmape.backend.almacen.repository;

import com.farmape.backend.almacen.model.MovimientoAlmacen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface MovimientoAlmacenRepository extends JpaRepository<MovimientoAlmacen, Long> {

    boolean existsByReferenciaTipoAndReferenciaId(String referenciaTipo, Integer referenciaId);

    long countByTipoMovimientoAndFechaMovimientoBetween(String tipoMovimiento, LocalDateTime desde, LocalDateTime hasta);
}
