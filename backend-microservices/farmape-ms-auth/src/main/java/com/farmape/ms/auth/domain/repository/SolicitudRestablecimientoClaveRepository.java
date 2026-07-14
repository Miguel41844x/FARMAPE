package com.farmape.ms.auth.auth.repository;

import com.farmape.ms.auth.auth.model.SolicitudRestablecimientoClave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudRestablecimientoClaveRepository extends JpaRepository<SolicitudRestablecimientoClave, Long> {
    List<SolicitudRestablecimientoClave> findTop50ByOrderByFechaSolicitudDesc();
}
