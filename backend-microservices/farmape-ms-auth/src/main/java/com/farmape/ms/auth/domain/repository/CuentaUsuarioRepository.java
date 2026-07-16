package com.farmape.ms.auth.domain.repository;

import com.farmape.ms.auth.domain.model.CuentaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuentaUsuarioRepository extends JpaRepository<CuentaUsuario, Integer> {

    Optional<CuentaUsuario> findByUsuario(String usuario);

    Optional<CuentaUsuario> findByEmail(String email);

    Optional<CuentaUsuario> findByUsuarioOrEmail(String usuario, String email);

    Optional<CuentaUsuario> findByTrabajador_IdTrabajador(Integer idTrabajador);

    boolean existsByUsuario(String usuario);

    boolean existsByEmail(String email);

    boolean existsByUsuarioAndIdCuentaNot(String usuario, Integer idCuenta);

    boolean existsByEmailAndIdCuentaNot(String email, Integer idCuenta);
}
