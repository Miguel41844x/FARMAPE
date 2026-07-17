package com.farmape.ms.ventas.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.farmape.ms.ventas.domain.model.Cliente;

public interface ClienteRepository extends MongoRepository<Cliente, String> {

    List<Cliente> findAllByOrderByFechaRegistroDesc();

    Optional<Cliente> findByDniRuc(String dniRuc);

    Optional<Cliente> findByIdCliente(Integer idCliente);

    boolean existsByDniRuc(String dniRuc);

    Optional<Cliente> findTopByOrderByIdClienteDesc();
}
