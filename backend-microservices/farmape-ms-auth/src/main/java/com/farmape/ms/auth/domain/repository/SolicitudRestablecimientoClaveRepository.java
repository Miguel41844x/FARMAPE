package com.farmape.ms.auth.domain.repository;

import com.farmape.ms.auth.domain.model.SolicitudRestablecimientoClave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudRestablecimientoClaveRepository extends JpaRepository<SolicitudRestablecimientoClave, Long> {
    List<SolicitudRestablecimientoClave> findTop50ByOrderByFechaSolicitudDesc();
}
