package com.farmape.backend.usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmape.backend.usuarios.model.CuentaUsuario;

import java.util.Optional;

public interface CuentaUsuarioRepository extends JpaRepository<CuentaUsuario, Integer> {

    Optional<CuentaUsuario> findByUsuario(String usuario);

    Optional<CuentaUsuario> findByEmail(String email);

    Optional<CuentaUsuario> findByUsuarioOrEmail(String usuario, String email);

    Optional<CuentaUsuario> findByTrabajador_IdTrabajador(Integer idTrabajador);

    boolean existsByUsuario(String usuario);

    boolean existsByEmail(String email);
}
