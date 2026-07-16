package com.farmape.ms.auth.domain.repository;

import com.farmape.ms.auth.domain.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByNombreRol(String nombreRol);
    Optional<Rol> findByCodigo(String codigo);
}