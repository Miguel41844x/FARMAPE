package com.farmape.backend.clientes.repository;

import com.farmape.backend.clientes.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByDniRuc(String dniRuc);

    boolean existsByDniRuc(String dniRuc);
}