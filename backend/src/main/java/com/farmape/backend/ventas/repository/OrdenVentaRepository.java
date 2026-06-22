package com.farmape.backend.ventas.repository;

import com.farmape.backend.ventas.enums.EstadoOrdenVenta;
import com.farmape.backend.ventas.model.OrdenVenta;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrdenVentaRepository extends JpaRepository<OrdenVenta, Integer> {

    List<OrdenVenta> findByEstado(EstadoOrdenVenta estado);
    List<OrdenVenta> findTop4ByOrderByIdOrdenVentaDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM OrdenVenta o WHERE o.idOrdenVenta = :idOrdenVenta")
    Optional<OrdenVenta> findByIdForUpdate(@Param("idOrdenVenta") Integer idOrdenVenta);
}
