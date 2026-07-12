package com.farmape.ms.inventario.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmape.ms.inventario.api.dto.CategoriaResponse;
import com.farmape.ms.inventario.api.dto.InformeAlmacenResponse;
import com.farmape.ms.inventario.api.dto.IngresoAlmacenRequest;
import com.farmape.ms.inventario.api.dto.IngresoAlmacenResponse;
import com.farmape.ms.inventario.api.dto.LoteProductoResponse;
import com.farmape.ms.inventario.api.dto.MovimientoAlmacenResponse;
import com.farmape.ms.inventario.api.dto.ProductoEstadoRequest;
import com.farmape.ms.inventario.api.dto.ProductoRequest;
import com.farmape.ms.inventario.api.dto.ProductoResponse;
import com.farmape.ms.inventario.api.dto.ResumenInventarioResponse;
import com.farmape.ms.inventario.api.dto.VerificacionProductoResponse;
import com.farmape.ms.inventario.application.exception.InventarioBusinessException;
import com.farmape.ms.inventario.application.exception.InventarioNotFoundException;
import com.farmape.ms.inventario.domain.model.Categoria;
import com.farmape.ms.inventario.domain.model.EstadoProducto;
import com.farmape.ms.inventario.domain.model.LoteProducto;
import com.farmape.ms.inventario.domain.model.MotivoMovimiento;
import com.farmape.ms.inventario.domain.model.MovimientoAlmacen;
import com.farmape.ms.inventario.domain.model.Producto;
import com.farmape.ms.inventario.domain.model.TipoMovimiento;
import com.farmape.ms.inventario.domain.model.VerificacionAlmacen;
import com.farmape.ms.inventario.domain.repository.CategoriaRepository;
import com.farmape.ms.inventario.domain.repository.LoteProductoRepository;
import com.farmape.ms.inventario.domain.repository.MovimientoAlmacenRepository;
import com.farmape.ms.inventario.domain.repository.ProductoRepository;
import com.farmape.ms.inventario.domain.repository.VerificacionAlmacenRepository;

@Service
@Transactional(readOnly = true)
public class InventarioConsultaService {

    private static final String REFERENCIA_RECEPCION = "RECEPCION";

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final LoteProductoRepository loteProductoRepository;
    private final MovimientoAlmacenRepository movimientoAlmacenRepository;
    private final VerificacionAlmacenRepository verificacionAlmacenRepository;

    public InventarioConsultaService(
            CategoriaRepository categoriaRepository,
            ProductoRepository productoRepository,
            LoteProductoRepository loteProductoRepository,
            MovimientoAlmacenRepository movimientoAlmacenRepository,
            VerificacionAlmacenRepository verificacionAlmacenRepository
    ) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.loteProductoRepository = loteProductoRepository;
        this.movimientoAlmacenRepository = movimientoAlmacenRepository;
        this.verificacionAlmacenRepository = verificacionAlmacenRepository;
    }

    public List<CategoriaResponse> listarCategoriasActivas() {
        return categoriaRepository.findByActivoTrueOrderByNombreAsc()
                .stream()
                .map(this::toCategoriaResponse)
                .toList();
    }

    public List<ProductoResponse> listarProductosActivos() {
        return productoRepository.findByEstadoOrderByNombreAsc(EstadoProducto.Activo)
                .stream()
                .map(this::toProductoResponse)
                .toList();
    }

    public List<ProductoResponse> listarProductos() {
        return productoRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(this::toProductoResponse)
                .toList();
    }

    public List<ProductoResponse> buscarProductos(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return listarProductosActivos();
        }

        return productoRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre.trim())
                .stream()
                .map(this::toProductoResponse)
                .toList();
    }

    public ProductoResponse obtenerProducto(Integer idProducto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new InventarioNotFoundException("Producto no encontrado: " + idProducto));

        return toProductoResponse(producto);
    }

    public List<ProductoResponse> listarProductosConStockBajo() {
        return productoRepository.findProductosConStockBajo()
                .stream()
                .map(this::toProductoResponse)
                .toList();
    }

    public List<LoteProductoResponse> listarLotesPorProducto(Integer idProducto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new InventarioNotFoundException("Producto no encontrado: " + idProducto));

        return loteProductoRepository.findByProductoAndStockDisponibleGreaterThanOrderByFechaVencimientoAsc(producto, 0)
                .stream()
                .map(this::toLoteProductoResponse)
                .toList();
    }

    public List<MovimientoAlmacenResponse> listarMovimientosRecientes() {
        return movimientoAlmacenRepository.findTop20ByOrderByFechaMovimientoDesc()
                .stream()
                .map(this::toMovimientoAlmacenResponse)
                .toList();
    }

    public List<MovimientoAlmacenResponse> listarMovimientosPorProducto(Integer idProducto) {
        if (!productoRepository.existsById(idProducto)) {
            throw new InventarioNotFoundException("Producto no encontrado: " + idProducto);
        }

        return movimientoAlmacenRepository.findTop20ByProductoIdProductoOrderByFechaMovimientoDesc(idProducto)
                .stream()
                .map(this::toMovimientoAlmacenResponse)
                .toList();
    }

    public ResumenInventarioResponse obtenerResumen() {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioDia = hoy.atStartOfDay();
        LocalDateTime finDia = hoy.atTime(LocalTime.MAX);

        List<ProductoResponse> productosStockBajo = listarProductosConStockBajo();
        List<LoteProductoResponse> proximosVencimientos = loteProductoRepository
                .findTop5ByStockDisponibleGreaterThanOrderByFechaVencimientoAsc(0)
                .stream()
                .map(this::toLoteProductoResponse)
                .toList();

        return new ResumenInventarioResponse(
                productoRepository.countByEstado(EstadoProducto.Activo),
                productosStockBajo.size(),
                loteProductoRepository.countByFechaVencimientoBetweenAndStockDisponibleGreaterThan(
                        hoy,
                        hoy.plusDays(30),
                        0
                ),
                movimientoAlmacenRepository.countByTipoMovimientoAndFechaMovimientoBetween(
                        TipoMovimiento.Entrada,
                        inicioDia,
                        finDia
                ),
                movimientoAlmacenRepository.countByTipoMovimientoAndFechaMovimientoBetween(
                        TipoMovimiento.Salida,
                        inicioDia,
                        finDia
                ),
                movimientoAlmacenRepository.countByTipoMovimientoAndFechaMovimientoBetween(
                        TipoMovimiento.Ajuste,
                        inicioDia,
                        finDia
                ),
                proximosVencimientos,
                listarMovimientosRecientes()
        );
    }

    @Transactional
    public ProductoResponse crearProducto(ProductoRequest request) {
        validarProductoRequest(request);

        Producto producto = new Producto();
        aplicarDatosProducto(producto, request);
        producto.setFechaCreacion(LocalDateTime.now());

        return toProductoResponse(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponse actualizarProducto(Integer idProducto, ProductoRequest request) {
        validarProductoRequest(request);

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new InventarioNotFoundException("Producto no encontrado: " + idProducto));
        aplicarDatosProducto(producto, request);

        return toProductoResponse(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponse cambiarEstadoProducto(Integer idProducto, ProductoEstadoRequest request) {
        if (request == null || request.estado() == null || request.estado().isBlank()) {
            throw new InventarioBusinessException("El estado es obligatorio.");
        }

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new InventarioNotFoundException("Producto no encontrado: " + idProducto));

        producto.setEstado(parseEstado(request.estado()));

        return toProductoResponse(productoRepository.save(producto));
    }

    public List<IngresoAlmacenResponse> listarIngresosAlmacen() {
        return movimientoAlmacenRepository.findTop20ByTipoMovimientoOrderByFechaMovimientoDesc(TipoMovimiento.Entrada)
                .stream()
                .map(this::toIngresoAlmacenResponse)
                .toList();
    }

    @Transactional
    public IngresoAlmacenResponse registrarIngresoAlmacen(IngresoAlmacenRequest request) {
        validarIngresoAlmacen(request);

        if (request.referenciaId() != null
                && movimientoAlmacenRepository.existsByReferenciaTipoAndReferenciaId(
                REFERENCIA_RECEPCION,
                request.referenciaId()
        )) {
            throw new InventarioBusinessException("Este ingreso ya fue registrado en almacen.");
        }

        Producto producto = productoRepository.findByIdForUpdate(request.idProducto())
                .orElseThrow(() -> new InventarioNotFoundException("Producto no encontrado: " + request.idProducto()));

        String numeroLote = normalizarLote(request.lote(), producto.getIdProducto());
        LocalDate fechaVencimientoCalculada = request.fechaVencimiento() != null
                ? request.fechaVencimiento()
                : producto.getFechaVencimiento();
        if (fechaVencimientoCalculada == null) {
            fechaVencimientoCalculada = LocalDate.now().plusYears(1);
        }
        final LocalDate fechaVencimiento = fechaVencimientoCalculada;

        LoteProducto lote = loteProductoRepository.findByProductoAndNumeroLote(producto, numeroLote)
                .orElseGet(() -> nuevoLote(producto, numeroLote, fechaVencimiento));
        lote.setFechaVencimiento(fechaVencimiento);
        lote.setStockDisponible(valorSeguro(lote.getStockDisponible()) + request.cantidad());
        lote = loteProductoRepository.save(lote);

        producto.setStockActual(valorSeguro(producto.getStockActual()) + request.cantidad());

        MovimientoAlmacen movimiento = new MovimientoAlmacen();
        movimiento.setProducto(producto);
        movimiento.setLote(lote);
        movimiento.setIdTrabajador(1);
        movimiento.setTipoMovimiento(TipoMovimiento.Entrada);
        movimiento.setMotivo(MotivoMovimiento.Compra);
        movimiento.setCantidad(request.cantidad());
        movimiento.setReferenciaTipo(REFERENCIA_RECEPCION);
        movimiento.setReferenciaId(request.referenciaId());
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setObservacion("Ingreso registrado desde modulo de almacen.");

        movimientoAlmacenRepository.save(movimiento);

        return new IngresoAlmacenResponse(
                request.referenciaId() != null ? request.referenciaId() : movimiento.getIdMovimiento(),
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

    public List<VerificacionProductoResponse> listarVerificacionesProductos() {
        return verificacionAlmacenRepository.findAllByOrderByFechaVerificacionDescIdVerificacionDesc()
                .stream()
                .map(this::toVerificacionProductoResponse)
                .toList();
    }

    @Transactional
    public VerificacionProductoResponse confirmarVerificacionProducto(Integer idVerificacion) {
        VerificacionAlmacen verificacion = obtenerVerificacion(idVerificacion);
        verificacion.setEstado("CONFORME");
        verificacion.setObservacion("CONFORME");

        return toVerificacionProductoResponse(verificacionAlmacenRepository.save(verificacion));
    }

    @Transactional
    public VerificacionProductoResponse observarVerificacionProducto(Integer idVerificacion) {
        VerificacionAlmacen verificacion = obtenerVerificacion(idVerificacion);
        verificacion.setEstado("OBSERVADO");
        verificacion.setObservacion("OBSERVADO");

        return toVerificacionProductoResponse(verificacionAlmacenRepository.save(verificacion));
    }

    public List<InformeAlmacenResponse> obtenerInformeAlmacen(String periodo) {
        LocalDateTime hasta = LocalDateTime.now();
        LocalDateTime desde = switch (periodo != null ? periodo.toUpperCase() : "HOY") {
            case "MES" -> hasta.minusMonths(1);
            case "SEMANA" -> hasta.minusWeeks(1);
            default -> hasta.toLocalDate().atStartOfDay();
        };

        long ingresos = movimientoAlmacenRepository.countByTipoMovimientoAndFechaMovimientoBetween(
                TipoMovimiento.Entrada,
                desde,
                hasta
        );
        long stockBajo = productoRepository.findProductosConStockBajo().size();
        long proximosVencer = loteProductoRepository.countByFechaVencimientoBetweenAndStockDisponibleGreaterThan(
                LocalDate.now(),
                LocalDate.now().plusDays(60),
                0
        );
        long verificacionesObservadas = verificacionAlmacenRepository.countByEstado("OBSERVADO");

        List<InformeAlmacenResponse> indicadores = new ArrayList<>();
        indicadores.add(new InformeAlmacenResponse(
                1,
                "Ingresos registrados",
                ingresos,
                "Movimientos de entrada del periodo seleccionado"
        ));
        indicadores.add(new InformeAlmacenResponse(
                2,
                "Productos con stock bajo",
                stockBajo,
                "Productos cuyo stock actual esta por debajo o igual al minimo"
        ));
        indicadores.add(new InformeAlmacenResponse(
                3,
                "Lotes proximos a vencer",
                proximosVencer,
                "Lotes con vencimiento dentro de los proximos 60 dias"
        ));
        indicadores.add(new InformeAlmacenResponse(
                4,
                "Verificaciones observadas",
                verificacionesObservadas,
                "Diferencias entre cantidad pedida y recibida"
        ));

        return indicadores;
    }

    private CategoriaResponse toCategoriaResponse(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getIdCategoria(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getActivo()
        );
    }

    private void validarProductoRequest(ProductoRequest request) {
        if (request == null) {
            throw new InventarioBusinessException("El producto es obligatorio.");
        }
        if (request.idCategoria() == null) {
            throw new InventarioBusinessException("La categoria es obligatoria.");
        }
        if (request.nombre() == null || request.nombre().isBlank()) {
            throw new InventarioBusinessException("El nombre del producto es obligatorio.");
        }
        if (request.precioVenta() == null || request.precioVenta().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InventarioBusinessException("El precio de venta debe ser mayor que cero.");
        }
        if (request.precioCompra() != null && request.precioCompra().compareTo(BigDecimal.ZERO) < 0) {
            throw new InventarioBusinessException("El precio de compra no puede ser negativo.");
        }
        if (request.stockActual() != null && request.stockActual() < 0) {
            throw new InventarioBusinessException("El stock actual no puede ser negativo.");
        }
        if (request.stockMinimo() != null && request.stockMinimo() < 0) {
            throw new InventarioBusinessException("El stock minimo no puede ser negativo.");
        }
    }

    private void aplicarDatosProducto(Producto producto, ProductoRequest request) {
        Categoria categoria = categoriaRepository.findById(request.idCategoria())
                .orElseThrow(() -> new InventarioNotFoundException("Categoria no encontrada: " + request.idCategoria()));

        producto.setCategoria(categoria);
        producto.setNombre(request.nombre().trim());
        producto.setDescripcion(request.descripcion());
        producto.setLaboratorio(request.laboratorio());
        producto.setPrecioCompra(request.precioCompra() != null ? request.precioCompra() : BigDecimal.ZERO);
        producto.setPrecioVenta(request.precioVenta());
        producto.setStockActual(request.stockActual() != null ? request.stockActual() : 0);
        producto.setStockMinimo(request.stockMinimo() != null ? request.stockMinimo() : 5);
        producto.setFechaVencimiento(request.fechaVencimiento());
        producto.setEstado(request.estado() != null && !request.estado().isBlank()
                ? parseEstado(request.estado())
                : EstadoProducto.Activo);
    }

    private EstadoProducto parseEstado(String estado) {
        try {
            return EstadoProducto.valueOf(estado.trim());
        } catch (IllegalArgumentException exception) {
            throw new InventarioBusinessException("Estado de producto invalido: " + estado);
        }
    }

    private void validarIngresoAlmacen(IngresoAlmacenRequest request) {
        if (request == null) {
            throw new InventarioBusinessException("El ingreso de almacen es obligatorio.");
        }
        if (request.idProducto() == null) {
            throw new InventarioBusinessException("El producto es obligatorio.");
        }
        if (request.cantidad() == null || request.cantidad() <= 0) {
            throw new InventarioBusinessException("La cantidad debe ser mayor que cero.");
        }
    }

    private LoteProducto nuevoLote(Producto producto, String numeroLote, LocalDate fechaVencimiento) {
        LoteProducto lote = new LoteProducto();
        lote.setProducto(producto);
        lote.setNumeroLote(numeroLote);
        lote.setFechaVencimiento(fechaVencimiento);
        lote.setCostoUnitario(producto.getPrecioCompra() != null ? producto.getPrecioCompra() : BigDecimal.ZERO);
        lote.setStockDisponible(0);
        lote.setEstado("Disponible");
        lote.setFechaIngreso(LocalDateTime.now());
        return lote;
    }

    private String normalizarLote(String lote, Integer idProducto) {
        if (lote != null && !lote.isBlank()) {
            return lote.trim();
        }
        return "LOTE-P" + idProducto;
    }

    private Integer valorSeguro(Integer valor) {
        return valor != null ? valor : 0;
    }

    private IngresoAlmacenResponse toIngresoAlmacenResponse(MovimientoAlmacen movimiento) {
        Producto producto = movimiento.getProducto();
        LoteProducto lote = movimiento.getLote();

        return new IngresoAlmacenResponse(
                movimiento.getReferenciaId() != null ? movimiento.getReferenciaId() : movimiento.getIdMovimiento(),
                producto != null ? producto.getIdProducto() : null,
                producto != null ? producto.getNombre() : null,
                movimiento.getCantidad(),
                lote != null ? lote.getNumeroLote() : null,
                lote != null ? lote.getFechaVencimiento() : null,
                null,
                null,
                "REGISTRADO"
        );
    }

    private VerificacionAlmacen obtenerVerificacion(Integer idVerificacion) {
        return verificacionAlmacenRepository.findById(idVerificacion)
                .orElseThrow(() -> new InventarioNotFoundException("Verificacion no encontrada: " + idVerificacion));
    }

    private VerificacionProductoResponse toVerificacionProductoResponse(VerificacionAlmacen verificacion) {
        Producto producto = verificacion.getProducto();

        return new VerificacionProductoResponse(
                verificacion.getIdVerificacion(),
                verificacion.getIdPedidoCompra(),
                producto != null ? producto.getIdProducto() : null,
                producto != null ? producto.getNombre() : null,
                verificacion.getCantidadPedida(),
                verificacion.getCantidadRecibida(),
                verificacion.getEstado(),
                verificacion.getObservacion()
        );
    }

    private ProductoResponse toProductoResponse(Producto producto) {
        Categoria categoria = producto.getCategoria();

        return new ProductoResponse(
                producto.getIdProducto(),
                categoria != null ? categoria.getIdCategoria() : null,
                categoria != null ? categoria.getNombre() : null,
                producto.getSku(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getLaboratorio(),
                producto.getPrecioCompra(),
                producto.getPrecioVenta(),
                producto.getStockActual(),
                producto.getStockMinimo(),
                producto.getFechaVencimiento(),
                producto.getRequiereReceta(),
                producto.getEstado() != null ? producto.getEstado().name() : null
        );
    }

    private LoteProductoResponse toLoteProductoResponse(LoteProducto lote) {
        Producto producto = lote.getProducto();

        return new LoteProductoResponse(
                lote.getIdLote(),
                producto != null ? producto.getIdProducto() : null,
                producto != null ? producto.getNombre() : null,
                lote.getNumeroLote(),
                lote.getFechaVencimiento(),
                lote.getCostoUnitario(),
                lote.getStockDisponible(),
                lote.getEstado(),
                lote.getFechaIngreso()
        );
    }

    private MovimientoAlmacenResponse toMovimientoAlmacenResponse(MovimientoAlmacen movimiento) {
        Producto producto = movimiento.getProducto();
        LoteProducto lote = movimiento.getLote();

        return new MovimientoAlmacenResponse(
                movimiento.getIdMovimiento(),
                producto != null ? producto.getIdProducto() : null,
                producto != null ? producto.getNombre() : null,
                lote != null ? lote.getIdLote() : null,
                lote != null ? lote.getNumeroLote() : null,
                movimiento.getIdTrabajador(),
                movimiento.getTipoMovimiento() != null ? movimiento.getTipoMovimiento().name() : null,
                movimiento.getMotivo() != null ? movimiento.getMotivo().name() : null,
                movimiento.getCantidad(),
                movimiento.getReferenciaTipo(),
                movimiento.getReferenciaId(),
                movimiento.getFechaMovimiento(),
                movimiento.getObservacion()
        );
    }
}
