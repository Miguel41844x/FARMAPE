package com.farmape.ms.ventas.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.farmape.ms.ventas.domain.model.EstadoVenta;
import com.farmape.ms.ventas.domain.model.Venta;

public interface VentaRepository extends MongoRepository<Venta, String> {

    List<Venta> findAllByOrderByFechaOrdenDesc();

    List<Venta> findByIdClienteOrderByFechaOrdenDesc(Integer idCliente);

    List<Venta> findByEstadoOrderByFechaOrdenDesc(EstadoVenta estado);

    Optional<Venta> findTopByOrderByIdOrdenVentaDesc();

    Optional<Venta> findByIdOrdenVenta(Integer idOrdenVenta);
}
