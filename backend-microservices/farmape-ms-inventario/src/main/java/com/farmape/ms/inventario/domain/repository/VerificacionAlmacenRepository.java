package com.farmape.ms.inventario.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmape.ms.inventario.domain.model.VerificacionAlmacen;

public interface VerificacionAlmacenRepository extends JpaRepository<VerificacionAlmacen, Integer> {

    List<VerificacionAlmacen> findAllByOrderByFechaVerificacionDescIdVerificacionDesc();

    long countByEstado(String estado);
}
