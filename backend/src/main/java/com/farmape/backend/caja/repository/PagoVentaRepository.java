package com.farmape.backend.caja.repository;

import com.farmape.backend.caja.model.PagoVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PagoVentaRepository extends JpaRepository<PagoVenta, Integer> {

    boolean existsByOrdenVenta_IdOrdenVenta(Integer idOrdenVenta);

    Optional<PagoVenta> findByOrdenVenta_IdOrdenVenta(Integer idOrdenVenta);
}