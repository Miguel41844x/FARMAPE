package com.farmape.backend.auth.repository;

import com.farmape.backend.auth.model.SolicitudRestablecimientoClave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudRestablecimientoClaveRepository extends JpaRepository<SolicitudRestablecimientoClave, Long> {
    List<SolicitudRestablecimientoClave> findTop50ByOrderByFechaSolicitudDesc();
}
