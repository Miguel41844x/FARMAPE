package com.farmape.backend.compras.repository;

import com.farmape.backend.compras.model.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Integer> {
    Optional<OrdenCompra> findTopByOrderByIdOrdenCompraDesc();
}
