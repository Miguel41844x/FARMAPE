package com.farmape.backend.almacen.service;

import com.farmape.backend.almacen.dto.*;
import com.farmape.backend.almacen.model.DetalleRecepcionCompra;
import com.farmape.backend.almacen.model.LoteProducto;
import com.farmape.backend.almacen.model.MovimientoAlmacen;
import com.farmape.backend.almacen.repository.DetalleRecepcionCompraRepository;
import com.farmape.backend.almacen.repository.LoteProductoRepository;
import com.farmape.backend.almacen.repository.MovimientoAlmacenRepository;
import com.farmape.backend.productos.model.Producto;
import com.farmape.backend.productos.repository.ProductoRepository;
import com.farmape.backend.security.AuthenticatedUserService;
import com.farmape.backend.trabajadores.model.Trabajador;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AlmacenService {

    private static final String REFERENCIA_RECEPCION = "RECEPCION";

    private final DetalleRecepcionCompraRepository detalleRecepcionCompraRepository;
    private final LoteProductoRepository loteProductoRepository;
    private final MovimientoAlmacenRepository movimientoAlmacenRepository;
    private final ProductoRepository productoRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public AlmacenService(
            DetalleRecepcionCompraRepository detalleRecepcionCompraRepository,
            LoteProductoRepository loteProductoRepository,
            MovimientoAlmacenRepository movimientoAlmacenRepository,
            ProductoRepository productoRepository,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.detalleRecepcionCompraRepository = detalleRecepcionCompraRepository;
        this.loteProductoRepository = loteProductoRepository;
        this.movimientoAlmacenRepository = movimientoAlmacenRepository;
        this.productoRepository = productoRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    public List<IngresoAlmacenResponse> listarIngresos() {
        return detalleRecepcionCompraRepository.findAll().stream()
                .sorted(Comparator.comparing(DetalleRecepcionCompra::getIdDetalleRecepcion).reversed())
                .map(this::toIngresoResponse)
                .toList();
    }

    @Transactional
    public IngresoAlmacenResponse registrarIngreso(RegistrarIngresoAlmacenRequest request) {
        Producto producto = productoRepository.findById(request.idProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (request.referenciaId() != null && movimientoAlmacenRepository.existsByReferenciaTipoAndReferenciaId(REFERENCIA_RECEPCION, request.referenciaId())) {
            throw new RuntimeException("Este ingreso ya fue registrado en almacén");
        }

        String numeroLote = normalizarLote(request.lote(), producto.getIdProducto());
        LocalDate fechaVencimientoCalculada = request.fechaVencimiento() != null
                ? request.fechaVencimiento()
                : producto.getFechaVencimiento();

        if (fechaVencimientoCalculada == null) {
            fechaVencimientoCalculada = LocalDate.now().plusYears(1);
        }
        final LocalDate fechaVencimiento = fechaVencimientoCalculada;

        LoteProducto lote = loteProductoRepository.findByProductoAndNumeroLote(producto, numeroLote)
                .orElseGet(() -> LoteProducto.builder()
                        .producto(producto)
                        .numeroLote(numeroLote)
                        .fechaVencimiento(fechaVencimiento)
                        .costoUnitario(producto.getPrecioCompra() != null ? producto.getPrecioCompra() : BigDecimal.ZERO)
                        .stockDisponible(0)
                        .estado("Disponible")
                        .fechaIngreso(LocalDateTime.now())
                        .build());

        lote.setFechaVencimiento(fechaVencimiento);
        lote.setStockDisponible((lote.getStockDisponible() != null ? lote.getStockDisponible() : 0) + request.cantidad());
        lote = loteProductoRepository.save(lote);

        producto.setStockActual((producto.getStockActual() != null ? producto.getStockActual() : 0) + request.cantidad());
        productoRepository.save(producto);

        Trabajador trabajador = authenticatedUserService.currentAccount().getTrabajador();

        movimientoAlmacenRepository.save(MovimientoAlmacen.builder()
                .producto(producto)
                .lote(lote)
                .trabajador(trabajador)
                .tipoMovimiento("Entrada")
                .motivo("Compra")
                .cantidad(request.cantidad())
                .referenciaTipo(REFERENCIA_RECEPCION)
                .referenciaId(request.referenciaId())
                .fechaMovimiento(LocalDateTime.now())
                .observacion("Ingreso registrado desde módulo de almacén")
                .build());

        return new IngresoAlmacenResponse(
                request.referenciaId(),
                producto.getIdProducto(),
                producto.getNombre(),
                request.cantidad(),
                lote.getNumeroLote(),
                lote.getFechaVencimiento(),
                request.idProveedor(),
                null,
                "REGISTRADO"
        );
    }

    public List<VerificacionProductoResponse> listarVerificaciones() {
        return detalleRecepcionCompraRepository.findAll().stream()
                .sorted(Comparator.comparing(DetalleRecepcionCompra::getIdDetalleRecepcion).reversed())
                .map(this::toVerificacionResponse)
                .toList();
    }

    @Transactional
    public VerificacionProductoResponse confirmarVerificacion(Integer idVerificacion) {
        DetalleRecepcionCompra detalle = obtenerDetalleRecepcion(idVerificacion);
        detalle.setObservacion("CONFORME");
        detalleRecepcionCompraRepository.save(detalle);
        return toVerificacionResponse(detalle);
    }

    @Transactional
    public VerificacionProductoResponse observarVerificacion(Integer idVerificacion) {
        DetalleRecepcionCompra detalle = obtenerDetalleRecepcion(idVerificacion);
        detalle.setObservacion("OBSERVADO");
        detalleRecepcionCompraRepository.save(detalle);
        return toVerificacionResponse(detalle);
    }

    public List<InformeAlmacenResponse> obtenerInforme(String periodo) {
        LocalDateTime hasta = LocalDateTime.now();
        LocalDateTime desde = switch (periodo != null ? periodo.toUpperCase() : "HOY") {
            case "MES" -> hasta.minusMonths(1);
            case "SEMANA" -> hasta.minusWeeks(1);
            default -> hasta.toLocalDate().atStartOfDay();
        };

        long ingresos = movimientoAlmacenRepository.countByTipoMovimientoAndFechaMovimientoBetween("Entrada", desde, hasta);
        long stockBajo = productoRepository.findAll().stream()
                .filter(p -> p.getStockActual() != null && p.getStockMinimo() != null && p.getStockActual() <= p.getStockMinimo())
                .count();
        long proximosVencer = loteProductoRepository.countByFechaVencimientoBetweenAndStockDisponibleGreaterThan(
                LocalDate.now(),
                LocalDate.now().plusDays(60),
                0
        );
        long verificacionesObservadas = detalleRecepcionCompraRepository.findAll().stream()
                .filter(detalle -> "OBSERVADO".equalsIgnoreCase(estadoVerificacion(detalle)))
                .count();

        List<InformeAlmacenResponse> indicadores = new ArrayList<>();
        indicadores.add(new InformeAlmacenResponse(1, "Ingresos registrados", ingresos, "Movimientos de entrada del periodo seleccionado"));
        indicadores.add(new InformeAlmacenResponse(2, "Productos con stock bajo", stockBajo, "Productos cuyo stock actual está por debajo o igual al mínimo"));
        indicadores.add(new InformeAlmacenResponse(3, "Lotes próximos a vencer", proximosVencer, "Lotes con vencimiento dentro de los próximos 60 días"));
        indicadores.add(new InformeAlmacenResponse(4, "Verificaciones observadas", verificacionesObservadas, "Diferencias entre cantidad pedida y recibida"));
        return indicadores;
    }

    private DetalleRecepcionCompra obtenerDetalleRecepcion(Integer idVerificacion) {
        return detalleRecepcionCompraRepository.findById(idVerificacion)
                .orElseThrow(() -> new RuntimeException("Verificación no encontrada"));
    }

    private IngresoAlmacenResponse toIngresoResponse(DetalleRecepcionCompra detalle) {
        Producto producto = detalle.getProducto();
        String proveedor = null;
        Integer idProveedor = null;

        if (detalle.getRecepcion() != null && detalle.getRecepcion().getOrdenCompra() != null && detalle.getRecepcion().getOrdenCompra().getProveedor() != null) {
            idProveedor = detalle.getRecepcion().getOrdenCompra().getProveedor().getIdProveedor();
            proveedor = detalle.getRecepcion().getOrdenCompra().getProveedor().getRazonSocial();
        }

        String lote = detalle.getLote() != null ? detalle.getLote().getNumeroLote() : normalizarLote(null, producto.getIdProducto());
        LocalDate fechaVencimiento = detalle.getLote() != null ? detalle.getLote().getFechaVencimiento() : producto.getFechaVencimiento();
        String estado = movimientoAlmacenRepository.existsByReferenciaTipoAndReferenciaId(REFERENCIA_RECEPCION, detalle.getIdDetalleRecepcion())
                ? "REGISTRADO"
                : "PENDIENTE";

        return new IngresoAlmacenResponse(
                detalle.getIdDetalleRecepcion(),
                producto.getIdProducto(),
                producto.getNombre(),
                detalle.getCantidadRecibida(),
                lote,
                fechaVencimiento,
                idProveedor,
                proveedor,
                estado
        );
    }

    private VerificacionProductoResponse toVerificacionResponse(DetalleRecepcionCompra detalle) {
        Integer idPedidoCompra = detalle.getRecepcion() != null && detalle.getRecepcion().getOrdenCompra() != null
                ? detalle.getRecepcion().getOrdenCompra().getIdOrdenCompra()
                : null;

        return new VerificacionProductoResponse(
                detalle.getIdDetalleRecepcion(),
                idPedidoCompra,
                detalle.getProducto().getIdProducto(),
                detalle.getProducto().getNombre(),
                detalle.getCantidadPedida(),
                detalle.getCantidadRecibida(),
                estadoVerificacion(detalle),
                detalle.getObservacion()
        );
    }

    private String estadoVerificacion(DetalleRecepcionCompra detalle) {
        if (detalle.getObservacion() != null && detalle.getObservacion().equalsIgnoreCase("CONFORME")) {
            return "CONFORME";
        }
        if (detalle.getObservacion() != null && detalle.getObservacion().equalsIgnoreCase("OBSERVADO")) {
            return "OBSERVADO";
        }
        return detalle.getCantidadPedida() != null && detalle.getCantidadPedida().equals(detalle.getCantidadRecibida())
                ? "CONFORME"
                : "OBSERVADO";
    }

    private String normalizarLote(String lote, Integer idProducto) {
        if (lote != null && !lote.isBlank()) {
            return lote.trim();
        }
        return "LOTE-P" + idProducto;
    }
}
