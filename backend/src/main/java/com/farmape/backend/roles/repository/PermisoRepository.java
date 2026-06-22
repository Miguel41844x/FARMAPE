package com.farmape.backend.roles.repository;

import com.farmape.backend.roles.model.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermisoRepository extends JpaRepository<Permiso, Integer> {
    List<Permiso> findAllByActivoTrueOrderByModuloAscNombreAsc();
}
