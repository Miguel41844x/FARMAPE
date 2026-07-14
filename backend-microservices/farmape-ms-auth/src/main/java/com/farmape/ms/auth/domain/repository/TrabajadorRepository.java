package com.farmape.ms.auth.trabajadores.repository;

import com.farmape.ms.auth.trabajadores.model.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrabajadorRepository extends JpaRepository<Trabajador, Integer> {

    Optional<Trabajador> findByDni(String dni);

    boolean existsByDni(String dni);

    boolean existsByDniAndIdTrabajadorNot(String dni, Integer idTrabajador);

    long countByRol_IdRol(Integer idRol);
}
