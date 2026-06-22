package com.farmape.backend.caja.service;

import com.farmape.backend.caja.dto.*;
import com.farmape.backend.caja.enums.EstadoPagoVenta;
import com.farmape.backend.caja.enums.TipoComprobante;
import com.farmape.backend.caja.model.ComprobanteVenta;
import com.farmape.backend.caja.model.PagoVenta;
import com.farmape.backend.caja.model.SerieComprobante;
import com.farmape.backend.caja.repository.ComprobanteVentaRepository;
import com.farmape.backend.caja.repository.PagoVentaRepository;
import com.farmape.backend.caja.repository.SerieComprobanteRepository;
import com.farmape.backend.productos.model.Producto;
import com.farmape.backend.productos.repository.ProductoRepository;
import com.farmape.backend.security.AuthenticatedUserService;
import com.farmape.backend.trabajadores.model.Trabajador;
import com.farmape.backend.ventas.dto.OrdenVentaResponse;
import com.farmape.backend.ventas.enums.EstadoOrdenVenta;
import com.farmape.backend.ventas.model.DetalleOrdenVenta;
import com.farmape.backend.ventas.model.OrdenVenta;
import com.farmape.backend.ventas.repository.DetalleOrdenVentaRepository;
import com.farmape.backend.ventas.repository.OrdenVentaRepository;
import com.farmape.backend.ventas.service.VentaService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CajaService {

    private final OrdenVentaRepository ordenVentaRepository;
    private final DetalleOrdenVentaRepository detalleOrdenVentaRepository;
    private final ProductoRepository productoRepository;
    private final PagoVentaRepository pagoVentaRepository;
    private final ComprobanteVentaRepository comprobanteVentaRepository;
    private final SerieComprobanteRepository serieComprobanteRepository;
    private final VentaService ventaService;
    private final AuthenticatedUserService authenticatedUserService;

    public CajaService(
            OrdenVentaRepository ordenVentaRepository,
            DetalleOrdenVentaRepository detalleOrdenVentaRepository,
            ProductoRepository productoRepository,
            PagoVentaRepository pagoVentaRepository,
            ComprobanteVentaRepository comprobanteVentaRepository,
            SerieComprobanteRepository serieComprobanteRepository,
            VentaService ventaService,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.ordenVentaRepository = ordenVentaRepository;
        this.detalleOrdenVentaRepository = detalleOrdenVentaRepository;
        this.productoRepository = productoRepository;
        this.pagoVentaRepository = pagoVentaRepository;
        this.comprobanteVentaRepository = comprobanteVentaRepository;
        this.serieComprobanteRepository = serieComprobanteRepository;
        this.ventaService = ventaService;
        this.authenticatedUserService = authenticatedUserService;
    }

    public List<OrdenVentaResponse> listarOrdenesPendientes() {
        return ventaService.listarConfirmadas();
    }

    public OrdenVentaResponse obtenerOrden(Integer idOrdenVenta) {
        return ventaService.obtenerPorId(idOrdenVenta);
    }

    @Transactional
    public RegistrarPagoResponse registrarPago(Integer idOrdenVenta, RegistrarPagoRequest request) {
        OrdenVenta orden = ordenVentaRepository.findById(idOrdenVenta)
                .orElseThrow(() -> new RuntimeException("Orden de venta no encontrada"));

        if (orden.getEstado() != EstadoOrdenVenta.Confirmada) {
            throw new RuntimeException("Solo se pueden pagar órdenes confirmadas");
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

        ventaService.revalidarStock(orden);

        Trabajador cajero = authenticatedUserService.currentAccount().getTrabajador();

        PagoVenta pago = PagoVenta.builder()
                .ordenVenta(orden)
                .cajero(cajero)
                .fechaPago(LocalDateTime.now())
                .montoPagado(request.montoPagado())
                .metodoPago(request.metodoPago())
                .estado(EstadoPagoVenta.Pagado)
                .build();

        pago = pagoVentaRepository.save(pago);

        SerieComprobante serie = obtenerYAvanzarSerie(request.tipoComprobante());

        ComprobanteVenta comprobante = ComprobanteVenta.builder()
                .ordenVenta(orden)
                .tipoComprobante(request.tipoComprobante())
                .serie(serie.getSerie())
                .numero(String.format("%08d", serie.getUltimoNumero()))
                .fechaEmision(LocalDateTime.now())
                .montoTotal(orden.getTotal())
                .build();

        comprobante = comprobanteVentaRepository.save(comprobante);

        descontarStock(orden);

        EstadoOrdenVenta anterior = orden.getEstado();
        orden.setEstado(EstadoOrdenVenta.Pagada);
        orden = ordenVentaRepository.save(orden);
        ventaService.registrarHistorial(orden, cajero, anterior, EstadoOrdenVenta.Pagada, "Pago registrado y comprobante emitido");

        return new RegistrarPagoResponse(
                ventaService.obtenerPorId(orden.getIdOrdenVenta()),
                toPagoResponse(pago),
                toComprobanteResponse(comprobante)
        );
    }

    private SerieComprobante obtenerYAvanzarSerie(TipoComprobante tipoComprobante) {
        SerieComprobante serie = serieComprobanteRepository
                .findFirstByTipoComprobanteAndActivoTrueOrderByIdSerieAsc(tipoComprobante)
                .orElseThrow(() -> new RuntimeException("No hay serie activa para " + tipoComprobante));
        serie.setUltimoNumero(serie.getUltimoNumero() + 1);
        return serieComprobanteRepository.save(serie);
    }

    private void descontarStock(OrdenVenta orden) {
        List<DetalleOrdenVenta> detalles = detalleOrdenVentaRepository.findByOrdenVenta(orden);
        for (DetalleOrdenVenta detalle : detalles) {
            Producto producto = detalle.getProducto();
            producto.setStockActual(producto.getStockActual() - detalle.getCantidad());
            productoRepository.save(producto);
        }
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
