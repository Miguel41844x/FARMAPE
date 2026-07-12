package com.farmape.ms.inventario.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmape.ms.inventario.api.dto.OrdenTiendaResponse;
import com.farmape.ms.inventario.api.dto.RepartoDomicilioResponse;
import com.farmape.ms.inventario.application.exception.InventarioNotFoundException;
import com.farmape.ms.inventario.domain.model.DespachoOperativo;
import com.farmape.ms.inventario.domain.repository.DespachoOperativoRepository;

@Service
@Transactional(readOnly = true)
public class DespachoOperativoService {

    private static final String TIPO_LOCAL = "LOCAL";
    private static final String TIPO_DOMICILIO = "DOMICILIO";

    private final DespachoOperativoRepository despachoOperativoRepository;

    public DespachoOperativoService(DespachoOperativoRepository despachoOperativoRepository) {
        this.despachoOperativoRepository = despachoOperativoRepository;
    }

    public List<OrdenTiendaResponse> listarOrdenesTienda() {
        return despachoOperativoRepository.findByTipoDespachoOrderByIdOrdenVentaDesc(TIPO_LOCAL)
                .stream()
                .map(this::toOrdenTiendaResponse)
                .toList();
    }

    @Transactional
    public OrdenTiendaResponse entregarOrdenTienda(Integer idOrdenVenta) {
        DespachoOperativo despacho = despachoOperativoRepository
                .findByTipoDespachoAndIdOrdenVenta(TIPO_LOCAL, idOrdenVenta)
                .orElseThrow(() -> new InventarioNotFoundException("Orden para entrega en tienda no encontrada: " + idOrdenVenta));

        despacho.setEstado("ENTREGADA");
        despacho.setFechaEntrega(LocalDateTime.now());

        return toOrdenTiendaResponse(despachoOperativoRepository.save(despacho));
    }

    public List<RepartoDomicilioResponse> listarRepartosDomicilio() {
        return despachoOperativoRepository.findByTipoDespachoOrderByIdOrdenVentaDesc(TIPO_DOMICILIO)
                .stream()
                .map(this::toRepartoDomicilioResponse)
                .toList();
    }

    @Transactional
    public RepartoDomicilioResponse entregarReparto(Integer idReparto) {
        DespachoOperativo despacho = despachoOperativoRepository.findById(idReparto)
                .orElseThrow(() -> new InventarioNotFoundException("Reparto no encontrado: " + idReparto));

        despacho.setEstado("ENTREGADO");
        despacho.setFechaEntrega(LocalDateTime.now());

        return toRepartoDomicilioResponse(despachoOperativoRepository.save(despacho));
    }

    private OrdenTiendaResponse toOrdenTiendaResponse(DespachoOperativo despacho) {
        return new OrdenTiendaResponse(
                despacho.getIdOrdenVenta(),
                despacho.getCliente(),
                despacho.getFechaOrden(),
                despacho.getTotal(),
                despacho.getEstado()
        );
    }

    private RepartoDomicilioResponse toRepartoDomicilioResponse(DespachoOperativo despacho) {
        return new RepartoDomicilioResponse(
                despacho.getIdDespacho(),
                despacho.getIdOrdenVenta(),
                despacho.getCliente(),
                despacho.getDireccion(),
                despacho.getRepartidor(),
                despacho.getEstado()
        );
    }
}
