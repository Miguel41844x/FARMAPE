package com.farmape.ms.inventario.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmape.ms.inventario.domain.model.DespachoOperativo;

public interface DespachoOperativoRepository extends JpaRepository<DespachoOperativo, Integer> {

    List<DespachoOperativo> findByTipoDespachoOrderByIdOrdenVentaDesc(String tipoDespacho);

    Optional<DespachoOperativo> findByTipoDespachoAndIdOrdenVenta(String tipoDespacho, Integer idOrdenVenta);
}
