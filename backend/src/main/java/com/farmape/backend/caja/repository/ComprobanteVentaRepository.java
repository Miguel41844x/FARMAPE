package com.farmape.backend.caja.repository;

import com.farmape.backend.caja.enums.TipoComprobante;
import com.farmape.backend.caja.model.ComprobanteVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComprobanteVentaRepository extends JpaRepository<ComprobanteVenta, Integer> {

    boolean existsByOrdenVenta_IdOrdenVenta(Integer idOrdenVenta);

    Optional<ComprobanteVenta> findByOrdenVenta_IdOrdenVenta(Integer idOrdenVenta);

    long countByTipoComprobante(TipoComprobante tipoComprobante);
}