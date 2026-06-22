package com.farmape.backend.caja.repository;

import com.farmape.backend.caja.enums.TipoComprobante;
import com.farmape.backend.caja.model.SerieComprobante;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface SerieComprobanteRepository extends JpaRepository<SerieComprobante, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SerieComprobante> findFirstByTipoComprobanteAndActivoTrueOrderByIdSerieAsc(TipoComprobante tipoComprobante);
}
