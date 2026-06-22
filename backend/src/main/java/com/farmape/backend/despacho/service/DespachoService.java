package com.farmape.backend.despacho.service;

import com.farmape.backend.despacho.dto.OrdenTiendaResponse;
import com.farmape.backend.despacho.dto.RepartoDomicilioResponse;
import com.farmape.backend.despacho.model.Despacho;
import com.farmape.backend.despacho.repository.DespachoRepository;
import com.farmape.backend.security.AuthenticatedUserService;
import com.farmape.backend.trabajadores.model.Trabajador;
import com.farmape.backend.ventas.enums.CanalPedido;
import com.farmape.backend.ventas.enums.EstadoOrdenVenta;
import com.farmape.backend.ventas.model.OrdenVenta;
import com.farmape.backend.ventas.repository.OrdenVentaRepository;
import com.farmape.backend.ventas.service.VentaService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class DespachoService {

    private final OrdenVentaRepository ordenVentaRepository;
    private final DespachoRepository despachoRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final VentaService ventaService;

    public DespachoService(
            OrdenVentaRepository ordenVentaRepository,
            DespachoRepository despachoRepository,
            AuthenticatedUserService authenticatedUserService,
            VentaService ventaService
    ) {
        this.ordenVentaRepository = ordenVentaRepository;
        this.despachoRepository = despachoRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.ventaService = ventaService;
    }

    public List<OrdenTiendaResponse> listarOrdenesTienda() {
        return ordenVentaRepository.findAll().stream()
                .filter(orden -> orden.getEstado() == EstadoOrdenVenta.Pagada || orden.getEstado() == EstadoOrdenVenta.Despachada)
                .filter(orden -> orden.getCanalPedido() == CanalPedido.Presencial)
                .sorted(Comparator.comparing(OrdenVenta::getIdOrdenVenta).reversed())
                .map(this::toOrdenTiendaResponse)
                .toList();
    }

    @Transactional
    public OrdenTiendaResponse entregarOrdenTienda(Integer idOrdenVenta) {
        OrdenVenta orden = obtenerOrden(idOrdenVenta);

        if (orden.getEstado() == EstadoOrdenVenta.Despachada) {
            return toOrdenTiendaResponse(orden);
        }

        if (orden.getEstado() != EstadoOrdenVenta.Pagada) {
            throw new RuntimeException("Solo se pueden entregar órdenes pagadas");
        }

        Trabajador trabajador = authenticatedUserService.currentAccount().getTrabajador();
        EstadoOrdenVenta anterior = orden.getEstado();
        orden.setEstado(EstadoOrdenVenta.Despachada);
        OrdenVenta ordenGuardada = ordenVentaRepository.save(orden);

        Despacho despacho = despachoRepository.findByOrdenVenta(ordenGuardada)
                .orElseGet(() -> Despacho.builder()
                        .ordenVenta(ordenGuardada)
                        .encargadoDespacho(trabajador)
                        .tipoDespacho("Local")
                        .fechaDespacho(LocalDateTime.now())
                        .estado("Pendiente")
                        .comprobanteVisado(false)
                        .build());

        despacho.setEncargadoDespacho(trabajador);
        despacho.setTipoDespacho("Local");
        despacho.setEstado("Entregado");
        despacho.setFechaEntrega(LocalDateTime.now());
        despacho.setComprobanteVisado(true);
        despachoRepository.save(despacho);

        ventaService.registrarHistorial(ordenGuardada, trabajador, anterior, EstadoOrdenVenta.Despachada, "Orden entregada en tienda");

        return toOrdenTiendaResponse(ordenGuardada);
    }

    public List<RepartoDomicilioResponse> listarRepartosDomicilio() {
        List<RepartoDomicilioResponse> repartosExistentes = despachoRepository.findAll().stream()
                .filter(despacho -> "Domicilio".equalsIgnoreCase(despacho.getTipoDespacho()))
                .map(this::toRepartoResponse)
                .toList();

        List<RepartoDomicilioResponse> repartosPorCrear = ordenVentaRepository.findAll().stream()
                .filter(orden -> orden.getEstado() == EstadoOrdenVenta.Pagada || orden.getEstado() == EstadoOrdenVenta.Despachada)
                .filter(orden -> orden.getCanalPedido() == CanalPedido.Telefono || orden.getCanalPedido() == CanalPedido.WhatsApp)
                .filter(orden -> despachoRepository.findByOrdenVenta(orden).isEmpty())
                .map(this::toRepartoPendienteResponse)
                .toList();

        return java.util.stream.Stream.concat(repartosExistentes.stream(), repartosPorCrear.stream())
                .sorted(Comparator.comparing(RepartoDomicilioResponse::idOrdenVenta).reversed())
                .toList();
    }

    @Transactional
    public RepartoDomicilioResponse entregarReparto(Integer idReparto) {
        Despacho despacho = despachoRepository.findById(idReparto)
                .orElseGet(() -> crearDespachoDomicilio(obtenerOrden(idReparto)));

        if ("Entregado".equalsIgnoreCase(despacho.getEstado())) {
            return toRepartoResponse(despacho);
        }

        OrdenVenta orden = despacho.getOrdenVenta();
        if (orden.getEstado() != EstadoOrdenVenta.Pagada && orden.getEstado() != EstadoOrdenVenta.Despachada) {
            throw new RuntimeException("Solo se pueden entregar repartos de órdenes pagadas");
        }

        Trabajador trabajador = authenticatedUserService.currentAccount().getTrabajador();
        EstadoOrdenVenta anterior = orden.getEstado();
        orden.setEstado(EstadoOrdenVenta.Despachada);
        ordenVentaRepository.save(orden);

        despacho.setEncargadoDespacho(trabajador);
        if (despacho.getRepartidor() == null) {
            despacho.setRepartidor(trabajador);
        }
        despacho.setEstado("Entregado");
        despacho.setFechaEntrega(LocalDateTime.now());
        despacho.setComprobanteVisado(true);
        despachoRepository.save(despacho);

        ventaService.registrarHistorial(orden, trabajador, anterior, EstadoOrdenVenta.Despachada, "Reparto a domicilio entregado");

        return toRepartoResponse(despacho);
    }

    @Transactional
    public RepartoDomicilioResponse crearRepartoDesdeOrden(Integer idOrdenVenta) {
        OrdenVenta orden = obtenerOrden(idOrdenVenta);
        if (orden.getEstado() != EstadoOrdenVenta.Pagada) {
            throw new RuntimeException("Solo se pueden preparar repartos de órdenes pagadas");
        }

        Trabajador trabajador = authenticatedUserService.currentAccount().getTrabajador();
        Despacho despacho = despachoRepository.findByOrdenVenta(orden)
                .orElseGet(() -> Despacho.builder()
                        .ordenVenta(orden)
                        .encargadoDespacho(trabajador)
                        .tipoDespacho("Domicilio")
                        .direccionEntrega(orden.getCliente().getDireccion())
                        .fechaDespacho(LocalDateTime.now())
                        .estado("Pendiente")
                        .comprobanteVisado(false)
                        .build());

        despacho.setTipoDespacho("Domicilio");
        despacho.setDireccionEntrega(orden.getCliente().getDireccion());
        despacho.setEstado(despacho.getEstado() == null ? "Pendiente" : despacho.getEstado());
        despacho = despachoRepository.save(despacho);
        return toRepartoResponse(despacho);
    }

    private Despacho crearDespachoDomicilio(OrdenVenta orden) {
        if (orden.getEstado() != EstadoOrdenVenta.Pagada && orden.getEstado() != EstadoOrdenVenta.Despachada) {
            throw new RuntimeException("Solo se pueden preparar repartos de órdenes pagadas");
        }

        Trabajador trabajador = authenticatedUserService.currentAccount().getTrabajador();
        return despachoRepository.save(Despacho.builder()
                .ordenVenta(orden)
                .encargadoDespacho(trabajador)
                .tipoDespacho("Domicilio")
                .direccionEntrega(orden.getCliente().getDireccion())
                .fechaDespacho(LocalDateTime.now())
                .estado("Pendiente")
                .comprobanteVisado(false)
                .build());
    }

    private OrdenVenta obtenerOrden(Integer idOrdenVenta) {
        return ordenVentaRepository.findById(idOrdenVenta)
                .orElseThrow(() -> new RuntimeException("Orden de venta no encontrada"));
    }

    private OrdenTiendaResponse toOrdenTiendaResponse(OrdenVenta orden) {
        return new OrdenTiendaResponse(
                orden.getIdOrdenVenta(),
                nombreCliente(orden),
                orden.getFechaOrden(),
                orden.getTotal(),
                orden.getEstado() == EstadoOrdenVenta.Despachada ? "ENTREGADA" : "PAGADA"
        );
    }

    private RepartoDomicilioResponse toRepartoResponse(Despacho despacho) {
        OrdenVenta orden = despacho.getOrdenVenta();
        return new RepartoDomicilioResponse(
                despacho.getIdDespacho(),
                orden.getIdOrdenVenta(),
                nombreCliente(orden),
                despacho.getDireccionEntrega() != null ? despacho.getDireccionEntrega() : orden.getCliente().getDireccion(),
                despacho.getRepartidor() != null ? despacho.getRepartidor().getNombres() + " " + despacho.getRepartidor().getApellidos() : null,
                estadoReparto(despacho)
        );
    }

    private RepartoDomicilioResponse toRepartoPendienteResponse(OrdenVenta orden) {
        return new RepartoDomicilioResponse(
                orden.getIdOrdenVenta(),
                orden.getIdOrdenVenta(),
                nombreCliente(orden),
                orden.getCliente().getDireccion(),
                null,
                orden.getEstado() == EstadoOrdenVenta.Despachada ? "ENTREGADO" : "PENDIENTE"
        );
    }

    private String estadoReparto(Despacho despacho) {
        if ("Entregado".equalsIgnoreCase(despacho.getEstado())) {
            return "ENTREGADO";
        }
        if ("En ruta".equalsIgnoreCase(despacho.getEstado()) || "En_ruta".equalsIgnoreCase(despacho.getEstado())) {
            return "EN_RUTA";
        }
        return "PENDIENTE";
    }

    private String nombreCliente(OrdenVenta orden) {
        return orden.getCliente().getNombres() + " " + (orden.getCliente().getApellidos() != null ? orden.getCliente().getApellidos() : "");
    }
}
