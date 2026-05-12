package com.farmape.backend.usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmape.backend.usuarios.model.CuentaUsuario;

import java.util.Optional;

public interface CuentaUsuarioRepository extends JpaRepository<CuentaUsuario, Integer> {

    Optional<CuentaUsuario> findByUsuario(String usuario);

    boolean existsByUsuario(String usuario);
}