package com.farmape.backend.caja.service;

import com.farmape.backend.caja.dto.*;
import com.farmape.backend.caja.enums.EstadoPagoVenta;
import com.farmape.backend.caja.enums.TipoComprobante;
import com.farmape.backend.caja.model.ComprobanteVenta;
import com.farmape.backend.caja.model.PagoVenta;
import com.farmape.backend.caja.repository.ComprobanteVentaRepository;
import com.farmape.backend.caja.repository.PagoVentaRepository;
import com.farmape.backend.trabajadores.model.Trabajador;
import com.farmape.backend.trabajadores.repository.TrabajadorRepository;
import com.farmape.backend.ventas.dto.OrdenVentaResponse;
import com.farmape.backend.ventas.enums.EstadoOrdenVenta;
import com.farmape.backend.ventas.model.OrdenVenta;
import com.farmape.backend.ventas.repository.OrdenVentaRepository;
import com.farmape.backend.ventas.service.VentaService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CajaService {

    private final OrdenVentaRepository ordenVentaRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final PagoVentaRepository pagoVentaRepository;
    private final ComprobanteVentaRepository comprobanteVentaRepository;
    private final VentaService ventaService;

    public CajaService(
            OrdenVentaRepository ordenVentaRepository,
            TrabajadorRepository trabajadorRepository,
            PagoVentaRepository pagoVentaRepository,
            ComprobanteVentaRepository comprobanteVentaRepository,
            VentaService ventaService
    ) {
        this.ordenVentaRepository = ordenVentaRepository;
        this.trabajadorRepository = trabajadorRepository;
        this.pagoVentaRepository = pagoVentaRepository;
        this.comprobanteVentaRepository = comprobanteVentaRepository;
        this.ventaService = ventaService;
    }

    public List<OrdenVentaResponse> listarOrdenesPendientes() {
        return ventaService.listarPendientes();
    }

    public OrdenVentaResponse obtenerOrden(Integer idOrdenVenta) {
        return ventaService.obtenerPorId(idOrdenVenta);
    }

    @Transactional
    public RegistrarPagoResponse registrarPago(Integer idOrdenVenta, RegistrarPagoRequest request) {
        OrdenVenta orden = ordenVentaRepository.findById(idOrdenVenta)
                .orElseThrow(() -> new RuntimeException("Orden de venta no encontrada"));

        if (orden.getEstado() != EstadoOrdenVenta.Pendiente) {
            throw new RuntimeException("Solo se pueden pagar órdenes pendientes");
        }

        if (pagoVentaRepository.existsByOrdenVenta_IdOrdenVenta(idOrdenVenta)) {
            throw new RuntimeException("La orden ya tiene un pago registrado");
        }

        if (comprobanteVentaRepository.existsByOrdenVenta_IdOrdenVenta(idOrdenVenta)) {
            throw new RuntimeException("La orden ya tiene un comprobante emitido");
        }

        if (request.montoPagado().compareTo(orden.getTotal()) < 0) {
            throw new RuntimeException("El monto pagado no cubre el total de la orden");
        }

        Trabajador cajero = trabajadorRepository.findById(request.idCajero())
                .orElseThrow(() -> new RuntimeException("Cajero no encontrado"));

        PagoVenta pago = PagoVenta.builder()
                .ordenVenta(orden)
                .cajero(cajero)
                .fechaPago(LocalDateTime.now())
                .montoPagado(request.montoPagado())
                .metodoPago(request.metodoPago())
                .estado(EstadoPagoVenta.Pagado)
                .build();

        pago = pagoVentaRepository.save(pago);

        ComprobanteVenta comprobante = ComprobanteVenta.builder()
                .ordenVenta(orden)
                .tipoComprobante(request.tipoComprobante())
                .serie(generarSerie(request.tipoComprobante()))
                .numero(generarNumero(request.tipoComprobante()))
                .fechaEmision(LocalDateTime.now())
                .montoTotal(orden.getTotal())
                .build();

        comprobante = comprobanteVentaRepository.save(comprobante);

        orden.setEstado(EstadoOrdenVenta.Pagada);
        orden = ordenVentaRepository.save(orden);

        return new RegistrarPagoResponse(
                ventaService.obtenerPorId(orden.getIdOrdenVenta()),
                toPagoResponse(pago),
                toComprobanteResponse(comprobante)
        );
    }

    private String generarSerie(TipoComprobante tipoComprobante) {
        return switch (tipoComprobante) {
            case Boleta -> "B001";
            case Factura -> "F001";
        };
    }

    private String generarNumero(TipoComprobante tipoComprobante) {
        long cantidad = comprobanteVentaRepository.countByTipoComprobante(tipoComprobante) + 1;
        return String.format("%08d", cantidad);
    }

    private PagoVentaResponse toPagoResponse(PagoVenta pago) {
        return new PagoVentaResponse(
                pago.getIdPagoVenta(),
                pago.getOrdenVenta().getIdOrdenVenta(),
                pago.getCajero().getIdTrabajador(),
                pago.getCajero().getNombres() + " " + pago.getCajero().getApellidos(),
                pago.getFechaPago(),
                pago.getMontoPagado(),
                pago.getMetodoPago(),
                pago.getEstado()
        );
    }

    private ComprobanteVentaResponse toComprobanteResponse(ComprobanteVenta comprobante) {
        return new ComprobanteVentaResponse(
                comprobante.getIdComprobante(),
                comprobante.getOrdenVenta().getIdOrdenVenta(),
                comprobante.getTipoComprobante(),
                comprobante.getSerie(),
                comprobante.getNumero(),
                comprobante.getFechaEmision(),
                comprobante.getMontoTotal()
        );
    }
}