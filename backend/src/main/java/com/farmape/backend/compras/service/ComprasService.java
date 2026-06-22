package com.farmape.backend.compras.service;

import com.farmape.backend.compras.dto.*;
import com.farmape.backend.compras.model.*;
import com.farmape.backend.compras.repository.*;
import com.farmape.backend.productos.model.Producto;
import com.farmape.backend.productos.repository.ProductoRepository;
import com.farmape.backend.security.AuthenticatedUserService;
import com.farmape.backend.trabajadores.model.Trabajador;
import com.farmape.backend.usuarios.model.CuentaUsuario;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class ComprasService {

    private final ProveedorRepository proveedorRepository;
    private final OrdenCompraRepository ordenCompraRepository;
    private final FacturaCompraRepository facturaCompraRepository;
    private final NotaCreditoRepository notaCreditoRepository;
    private final PagoProveedorRepository pagoProveedorRepository;
    private final ProductoRepository productoRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public ComprasService(ProveedorRepository proveedorRepository,
                          OrdenCompraRepository ordenCompraRepository,
                          FacturaCompraRepository facturaCompraRepository,
                          NotaCreditoRepository notaCreditoRepository,
                          PagoProveedorRepository pagoProveedorRepository,
                          ProductoRepository productoRepository,
                          AuthenticatedUserService authenticatedUserService) {
        this.proveedorRepository = proveedorRepository;
        this.ordenCompraRepository = ordenCompraRepository;
        this.facturaCompraRepository = facturaCompraRepository;
        this.notaCreditoRepository = notaCreditoRepository;
        this.pagoProveedorRepository = pagoProveedorRepository;
        this.productoRepository = productoRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Transactional(readOnly = true)
    public List<ProveedorResponse> listarProveedores() {
        return proveedorRepository.findAll(Sort.by(Sort.Direction.ASC, "razonSocial"))
                .stream()
                .map(this::toProveedorResponse)
                .toList();
    }

    @Transactional
    public ProveedorResponse crearProveedor(ProveedorRequest request) {
        String ruc = limpiar(request.ruc());
        if (proveedorRepository.existsByRuc(ruc)) {
            throw new IllegalArgumentException("Ya existe un proveedor con ese RUC");
        }

        Proveedor proveedor = Proveedor.builder()
                .ruc(ruc)
                .razonSocial(limpiar(request.razonSocial()))
                .telefono(limpiar(request.telefono()))
                .email(limpiar(request.email()))
                .direccion(limpiar(request.direccion()))
                .tipoProveedor(defaultTexto(request.tipoProveedor(), "Proveedor"))
                .activo(request.activo() != null ? request.activo() : Boolean.TRUE)
                .build();

        return toProveedorResponse(proveedorRepository.save(proveedor));
    }

    @Transactional
    public ProveedorResponse actualizarProveedor(Integer idProveedor, ProveedorRequest request) {
        Proveedor proveedor = buscarProveedor(idProveedor);
        String ruc = limpiar(request.ruc());

        proveedorRepository.findByRuc(ruc)
                .filter(existente -> !existente.getIdProveedor().equals(idProveedor))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ya existe otro proveedor con ese RUC");
                });

        proveedor.setRuc(ruc);
        proveedor.setRazonSocial(limpiar(request.razonSocial()));
        proveedor.setTelefono(limpiar(request.telefono()));
        proveedor.setEmail(limpiar(request.email()));
        proveedor.setDireccion(limpiar(request.direccion()));
        proveedor.setTipoProveedor(defaultTexto(request.tipoProveedor(), "Proveedor"));
        proveedor.setActivo(request.activo() != null ? request.activo() : Boolean.TRUE);

        return toProveedorResponse(proveedorRepository.save(proveedor));
    }

    @Transactional
    public void eliminarProveedor(Integer idProveedor) {
        Proveedor proveedor = buscarProveedor(idProveedor);
        proveedor.setActivo(Boolean.FALSE);
        proveedorRepository.save(proveedor);
    }

    @Transactional(readOnly = true)
    public List<OrdenCompraResponse> listarOrdenesCompra() {
        return ordenCompraRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaOrden"))
                .stream()
                .map(this::toOrdenResponse)
                .toList();
    }

    @Transactional
    public OrdenCompraResponse crearOrdenCompra(CrearOrdenCompraRequest request) {
        if (request.detalles() == null || request.detalles().isEmpty()) {
            throw new IllegalArgumentException("La orden debe incluir al menos un producto");
        }

        Proveedor proveedor = buscarProveedor(request.idProveedor());
        if (!Boolean.TRUE.equals(proveedor.getActivo())) {
            throw new IllegalStateException("El proveedor seleccionado está inactivo");
        }

        Trabajador administrador = currentTrabajador();
        OrdenCompra orden = OrdenCompra.builder()
                .numeroOrden(generarNumeroOrden())
                .proveedor(proveedor)
                .administrador(administrador)
                .fechaOrden(LocalDateTime.now())
                .fechaEntrega(request.fechaEntrega())
                .medioPedido(defaultTexto(request.medioPedido(), "Web"))
                .estado("Pendiente")
                .total(BigDecimal.ZERO)
                .observacion(defaultTexto(request.observacion(), request.observaciones()))
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (DetalleOrdenCompraRequest detalleRequest : request.detalles()) {
            Producto producto = productoRepository.findById(detalleRequest.idProducto())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + detalleRequest.idProducto()));

            BigDecimal subtotal = detalleRequest.precioUnitario()
                    .multiply(BigDecimal.valueOf(detalleRequest.cantidad()))
                    .setScale(2, RoundingMode.HALF_UP);
            total = total.add(subtotal);

            orden.agregarDetalle(DetalleOrdenCompra.builder()
                    .producto(producto)
                    .cantidad(detalleRequest.cantidad())
                    .precioUnitario(detalleRequest.precioUnitario())
                    .subtotal(subtotal)
                    .build());
        }

        orden.setTotal(total.setScale(2, RoundingMode.HALF_UP));
        return toOrdenResponse(ordenCompraRepository.save(orden));
    }

    @Transactional(readOnly = true)
    public List<FacturaProveedorResponse> listarFacturasProveedor() {
        return facturaCompraRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaRegistro"))
                .stream()
                .map(this::toFacturaResponse)
                .toList();
    }

    @Transactional
    public FacturaProveedorResponse registrarFacturaProveedor(RegistrarFacturaProveedorRequest request) {
        OrdenCompra orden = ordenCompraRepository.findById(request.idOrdenCompra())
                .orElseThrow(() -> new IllegalArgumentException("Orden de compra no encontrada"));

        String numeroFactura = construirNumeroFactura(request.serie(), request.numero());
        if (facturaCompraRepository.existsByNumeroFactura(numeroFactura)) {
            throw new IllegalArgumentException("Ya existe una factura con ese número");
        }

        BigDecimal total = request.total().setScale(2, RoundingMode.HALF_UP);
        BigDecimal subtotal = total.divide(BigDecimal.valueOf(1.18), 2, RoundingMode.HALF_UP);
        BigDecimal igv = total.subtract(subtotal).setScale(2, RoundingMode.HALF_UP);
        String tipoPago = defaultTexto(request.tipoPago(), request.condicionPago());
        if (tipoPago == null || tipoPago.isBlank()) tipoPago = "Contado";

        FacturaCompra factura = FacturaCompra.builder()
                .ordenCompra(orden)
                .numeroFactura(numeroFactura)
                .fechaEmision(request.fechaEmision())
                .fechaRegistro(LocalDateTime.now())
                .tipoPago(normalizarTitulo(tipoPago))
                .diasCredito(request.diasCredito())
                .fechaVencimiento(request.fechaVencimiento())
                .subtotal(subtotal)
                .igv(igv)
                .montoTotal(total)
                .estado("Pendiente")
                .build();

        orden.setEstado("Facturada");
        ordenCompraRepository.save(orden);
        return toFacturaResponse(facturaCompraRepository.save(factura));
    }

    @Transactional(readOnly = true)
    public List<NotaCreditoResponse> listarNotasCredito() {
        return notaCreditoRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaEmision"))
                .stream()
                .map(this::toNotaResponse)
                .toList();
    }

    @Transactional
    public NotaCreditoResponse registrarNotaCredito(RegistrarNotaCreditoRequest request) {
        FacturaCompra factura = buscarFactura(request.idFacturaProveedor());
        NotaCredito nota = NotaCredito.builder()
                .facturaCompra(factura)
                .numeroNota(generarNumeroNota())
                .fechaEmision(LocalDate.now())
                .motivo(normalizarTitulo(request.motivo()))
                .descripcion(limpiar(request.descripcion()))
                .monto(request.monto().setScale(2, RoundingMode.HALF_UP))
                .build();

        factura.setEstado("Con nota de crédito");
        facturaCompraRepository.save(factura);
        return toNotaResponse(notaCreditoRepository.save(nota));
    }

    @Transactional(readOnly = true)
    public List<PagoProveedorResponse> listarPagosProveedor() {
        return pagoProveedorRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaPago"))
                .stream()
                .map(this::toPagoResponse)
                .toList();
    }

    @Transactional
    public PagoProveedorResponse registrarPagoProveedor(RegistrarPagoProveedorRequest request) {
        FacturaCompra factura = buscarFactura(request.idFacturaProveedor());
        BigDecimal montoPagado = request.monto().setScale(2, RoundingMode.HALF_UP);
        if (montoPagado.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto pagado debe ser mayor a cero");
        }

        BigDecimal totalPagadoActual = pagoProveedorRepository.totalPagado(factura);
        BigDecimal nuevoTotalPagado = totalPagadoActual.add(montoPagado);
        if (nuevoTotalPagado.compareTo(factura.getMontoTotal()) > 0) {
            throw new IllegalArgumentException("El pago supera el saldo pendiente de la factura");
        }

        PagoProveedor pago = PagoProveedor.builder()
                .facturaCompra(factura)
                .administrador(currentTrabajador())
                .fechaPago(request.fechaPago() != null ? request.fechaPago().atStartOfDay() : LocalDateTime.now())
                .montoPagado(montoPagado)
                .metodoPago(normalizarTitulo(defaultTexto(request.metodoPago(), "Transferencia")))
                .referenciaOperacion(limpiar(request.referencia()))
                .observacion(limpiar(request.observacion()))
                .build();

        if (nuevoTotalPagado.compareTo(factura.getMontoTotal()) == 0) {
            factura.setEstado("Pagada");
        } else {
            factura.setEstado("Pago parcial");
        }
        facturaCompraRepository.save(factura);

        return toPagoResponse(pagoProveedorRepository.save(pago));
    }

    private Proveedor buscarProveedor(Integer idProveedor) {
        return proveedorRepository.findById(idProveedor)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));
    }

    private FacturaCompra buscarFactura(Integer idFacturaProveedor) {
        return facturaCompraRepository.findById(idFacturaProveedor)
                .orElseThrow(() -> new IllegalArgumentException("Factura de proveedor no encontrada"));
    }

    private Trabajador currentTrabajador() {
        CuentaUsuario cuenta = authenticatedUserService.currentAccount();
        Trabajador trabajador = cuenta.getTrabajador();
        if (trabajador == null) {
            throw new IllegalStateException("La cuenta autenticada no tiene trabajador asociado");
        }
        return trabajador;
    }

    private String generarNumeroOrden() {
        int siguiente = ordenCompraRepository.findTopByOrderByIdOrdenCompraDesc()
                .map(orden -> orden.getIdOrdenCompra() + 1)
                .orElse(1);
        return "OC-" + String.format("%06d", siguiente);
    }

    private String generarNumeroNota() {
        int siguiente = notaCreditoRepository.findTopByOrderByIdNotaCreditoDesc()
                .map(nota -> nota.getIdNotaCredito() + 1)
                .orElse(1);
        return "NC-" + String.format("%06d", siguiente);
    }

    private String construirNumeroFactura(String serie, String numero) {
        String nro = limpiar(numero);
        String ser = limpiar(serie);
        return ser == null || ser.isBlank() ? nro : ser + "-" + nro;
    }

    private String[] partirNumero(String numeroCompleto) {
        if (numeroCompleto == null || !numeroCompleto.contains("-")) {
            return new String[] {"", numeroCompleto};
        }
        String[] partes = numeroCompleto.split("-", 2);
        return new String[] {partes[0], partes[1]};
    }

    private ProveedorResponse toProveedorResponse(Proveedor proveedor) {
        if (proveedor == null) return null;
        return new ProveedorResponse(
                proveedor.getIdProveedor(),
                proveedor.getRuc(),
                proveedor.getRazonSocial(),
                proveedor.getTelefono(),
                proveedor.getEmail(),
                proveedor.getDireccion(),
                proveedor.getTipoProveedor(),
                proveedor.getActivo()
        );
    }

    private OrdenCompraResponse toOrdenResponse(OrdenCompra orden) {
        return new OrdenCompraResponse(
                orden.getIdOrdenCompra(),
                orden.getNumeroOrden(),
                orden.getNumeroOrden(),
                toProveedorResponse(orden.getProveedor()),
                orden.getProveedor().getRazonSocial(),
                orden.getFechaOrden(),
                orden.getFechaOrden(),
                orden.getFechaEntrega(),
                orden.getMedioPedido(),
                orden.getEstado(),
                orden.getTotal(),
                orden.getObservacion(),
                orden.getDetalles() == null ? List.of() : orden.getDetalles().stream().map(this::toDetalleResponse).toList()
        );
    }

    private DetalleOrdenCompraResponse toDetalleResponse(DetalleOrdenCompra detalle) {
        return new DetalleOrdenCompraResponse(
                detalle.getIdDetalleCompra(),
                detalle.getProducto().getIdProducto(),
                detalle.getProducto().getNombre(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getSubtotal()
        );
    }

    private FacturaProveedorResponse toFacturaResponse(FacturaCompra factura) {
        String[] partes = partirNumero(factura.getNumeroFactura());
        return new FacturaProveedorResponse(
                factura.getIdFacturaCompra(),
                factura.getIdFacturaCompra(),
                factura.getOrdenCompra().getIdOrdenCompra(),
                partes[0],
                partes[1],
                factura.getNumeroFactura(),
                toProveedorResponse(factura.getOrdenCompra().getProveedor()),
                factura.getOrdenCompra().getProveedor().getRazonSocial(),
                factura.getFechaEmision(),
                factura.getFechaRegistro(),
                factura.getFechaVencimiento(),
                factura.getTipoPago(),
                factura.getTipoPago(),
                factura.getSubtotal(),
                factura.getIgv(),
                factura.getMontoTotal(),
                factura.getMontoTotal(),
                factura.getEstado()
        );
    }

    private NotaCreditoResponse toNotaResponse(NotaCredito nota) {
        return new NotaCreditoResponse(
                nota.getIdNotaCredito(),
                nota.getIdNotaCredito(),
                nota.getFacturaCompra().getIdFacturaCompra(),
                nota.getNumeroNota(),
                nota.getNumeroNota(),
                toFacturaResponse(nota.getFacturaCompra()),
                nota.getMotivo(),
                nota.getDescripcion(),
                nota.getFechaEmision(),
                nota.getMonto()
        );
    }

    private PagoProveedorResponse toPagoResponse(PagoProveedor pago) {
        return new PagoProveedorResponse(
                pago.getIdPagoProveedor(),
                pago.getFacturaCompra().getIdFacturaCompra(),
                toFacturaResponse(pago.getFacturaCompra()),
                pago.getFechaPago(),
                pago.getMetodoPago(),
                pago.getReferenciaOperacion(),
                pago.getMontoPagado(),
                pago.getMontoPagado(),
                pago.getObservacion()
        );
    }

    private String limpiar(String valor) {
        return valor == null ? null : valor.trim();
    }

    private String defaultTexto(String valor, String defecto) {
        String limpio = limpiar(valor);
        return limpio == null || limpio.isBlank() ? defecto : limpio;
    }

    private String normalizarTitulo(String valor) {
        String limpio = defaultTexto(valor, "").replace('_', ' ').trim().toLowerCase(Locale.ROOT);
        if (limpio.isBlank()) return limpio;
        return Character.toUpperCase(limpio.charAt(0)) + limpio.substring(1);
    }
}
