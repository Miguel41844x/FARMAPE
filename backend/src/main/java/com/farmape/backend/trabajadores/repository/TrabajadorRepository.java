package com.farmape.backend.trabajadores.repository;

import com.farmape.backend.trabajadores.model.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrabajadorRepository extends JpaRepository<Trabajador, Integer> {

    Optional<Trabajador> findByDni(String dni);

    boolean existsByDni(String dni);
}