package com.farmape.backend.compras.repository;

import com.farmape.backend.compras.model.NotaCredito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotaCreditoRepository extends JpaRepository<NotaCredito, Integer> {
    Optional<NotaCredito> findTopByOrderByIdNotaCreditoDesc();
}
