package com.farmape.backend.compras.repository;

import com.farmape.backend.compras.model.FacturaCompra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacturaCompraRepository extends JpaRepository<FacturaCompra, Integer> {
    boolean existsByNumeroFactura(String numeroFactura);
    Optional<FacturaCompra> findByNumeroFactura(String numeroFactura);
}
