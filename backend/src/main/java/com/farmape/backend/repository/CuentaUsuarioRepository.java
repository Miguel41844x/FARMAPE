package com.farmape.backend.repository;

import com.farmape.backend.model.CuentaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuentaUsuarioRepository extends JpaRepository<CuentaUsuario, Integer> {

    Optional<CuentaUsuario> findByEmail(String email);

    boolean existsByEmail(String usuario);
}