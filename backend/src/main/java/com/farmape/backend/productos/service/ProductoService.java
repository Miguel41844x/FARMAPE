package com.farmape.backend.productos.service;

import com.farmape.backend.productos.dto.ProductoRequest;
import com.farmape.backend.productos.dto.ProductoResponse;
import com.farmape.backend.productos.enums.EstadoProducto;
import com.farmape.backend.productos.model.Categoria;
import com.farmape.backend.productos.model.Producto;
import com.farmape.backend.productos.repository.CategoriaRepository;
import com.farmape.backend.productos.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository
    ) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<ProductoResponse> listar() {
        return productoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProductoResponse> listarActivos() {
        return productoRepository.findByEstado(EstadoProducto.Activo)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProductoResponse> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductoResponse obtenerPorId(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return toResponse(producto);
    }

    public ProductoResponse crear(ProductoRequest request) {
        Categoria categoria = categoriaRepository.findById(request.idCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        Producto producto = Producto.builder()
                .categoria(categoria)
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .precioVenta(request.precioVenta())
                .stockActual(request.stockActual())
                .estado(request.estado() != null ? request.estado() : EstadoProducto.Activo)
                .build();

        return toResponse(productoRepository.save(producto));
    }

    public ProductoResponse actualizar(Integer id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Categoria categoria = categoriaRepository.findById(request.idCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        producto.setCategoria(categoria);
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecioVenta(request.precioVenta());
        producto.setStockActual(request.stockActual());
        producto.setEstado(request.estado() != null ? request.estado() : producto.getEstado());

        return toResponse(productoRepository.save(producto));
    }

    private ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(
                producto.getIdProducto(),
                producto.getCategoria().getIdCategoria(),
                producto.getCategoria().getNombre(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getLaboratorio(),
                producto.getPrecioCompra(),
                producto.getPrecioVenta(),
                producto.getStockActual(),
                producto.getStockMinimo(),
                producto.getFechaVencimiento(),
                producto.getEstado()
        );
    }
}