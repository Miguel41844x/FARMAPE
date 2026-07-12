package com.farmape.ms.inventario.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmape.ms.inventario.api.dto.CategoriaResponse;
import com.farmape.ms.inventario.api.dto.LoteProductoResponse;
import com.farmape.ms.inventario.api.dto.MovimientoAlmacenResponse;
import com.farmape.ms.inventario.api.dto.ProductoResponse;
import com.farmape.ms.inventario.application.exception.InventarioNotFoundException;
import com.farmape.ms.inventario.domain.model.Categoria;
import com.farmape.ms.inventario.domain.model.EstadoProducto;
import com.farmape.ms.inventario.domain.model.LoteProducto;
import com.farmape.ms.inventario.domain.model.MovimientoAlmacen;
import com.farmape.ms.inventario.domain.model.Producto;
import com.farmape.ms.inventario.domain.repository.CategoriaRepository;
import com.farmape.ms.inventario.domain.repository.LoteProductoRepository;
import com.farmape.ms.inventario.domain.repository.MovimientoAlmacenRepository;
import com.farmape.ms.inventario.domain.repository.ProductoRepository;

@Service
@Transactional(readOnly = true)
public class InventarioConsultaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final LoteProductoRepository loteProductoRepository;
    private final MovimientoAlmacenRepository movimientoAlmacenRepository;

    public InventarioConsultaService(
            CategoriaRepository categoriaRepository,
            ProductoRepository productoRepository,
            LoteProductoRepository loteProductoRepository,
            MovimientoAlmacenRepository movimientoAlmacenRepository
    ) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.loteProductoRepository = loteProductoRepository;
        this.movimientoAlmacenRepository = movimientoAlmacenRepository;
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

    private CategoriaResponse toCategoriaResponse(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getIdCategoria(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getActivo()
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
