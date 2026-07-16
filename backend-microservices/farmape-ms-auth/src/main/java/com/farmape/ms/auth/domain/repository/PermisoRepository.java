package com.farmape.ms.auth.domain.repository;

import com.farmape.ms.auth.domain.model.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermisoRepository extends JpaRepository<Permiso, Integer> {
    List<Permiso> findAllByActivoTrueOrderByModuloAscNombreAsc();
}
