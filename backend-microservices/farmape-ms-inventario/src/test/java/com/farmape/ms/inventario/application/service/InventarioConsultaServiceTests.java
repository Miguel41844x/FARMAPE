package com.farmape.ms.inventario.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.farmape.ms.inventario.api.dto.ProductoResponse;
import com.farmape.ms.inventario.application.exception.InventarioNotFoundException;
import com.farmape.ms.inventario.domain.model.Categoria;
import com.farmape.ms.inventario.domain.model.EstadoProducto;
import com.farmape.ms.inventario.domain.model.Producto;
import com.farmape.ms.inventario.domain.repository.CategoriaRepository;
import com.farmape.ms.inventario.domain.repository.LoteProductoRepository;
import com.farmape.ms.inventario.domain.repository.ProductoRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventarioConsultaServiceTests {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private LoteProductoRepository loteProductoRepository;

    @InjectMocks
    private InventarioConsultaService inventarioConsultaService;

    @Test
    void listarProductosActivosMapsDomainToResponse() {
        Producto producto = producto();

        when(productoRepository.findByEstadoOrderByNombreAsc(EstadoProducto.Activo))
                .thenReturn(List.of(producto));

        List<ProductoResponse> responses = inventarioConsultaService.listarProductosActivos();

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().idProducto()).isEqualTo(1);
        assertThat(responses.getFirst().idCategoria()).isEqualTo(10);
        assertThat(responses.getFirst().categoria()).isEqualTo("Analgesicos");
        assertThat(responses.getFirst().nombre()).isEqualTo("Paracetamol 500mg");
        assertThat(responses.getFirst().stockActual()).isEqualTo(56);
        assertThat(responses.getFirst().estado()).isEqualTo("Activo");
    }

    @Test
    void obtenerProductoThrowsWhenProductoDoesNotExist() {
        when(productoRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventarioConsultaService.obtenerProducto(999))
                .isInstanceOf(InventarioNotFoundException.class)
                .hasMessageContaining("Producto no encontrado: 999");
    }

    private Producto producto() {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(10);
        categoria.setNombre("Analgesicos");
        categoria.setActivo(true);

        Producto producto = new Producto();
        producto.setIdProducto(1);
        producto.setCategoria(categoria);
        producto.setSku("PAR-500");
        producto.setNombre("Paracetamol 500mg");
        producto.setDescripcion("Tabletas para dolor y fiebre");
        producto.setLaboratorio("Medifarma");
        producto.setPrecioCompra(new BigDecimal("0.35"));
        producto.setPrecioVenta(new BigDecimal("1.00"));
        producto.setStockActual(56);
        producto.setStockMinimo(30);
        producto.setFechaVencimiento(LocalDate.of(2026, 10, 23));
        producto.setRequiereReceta(false);
        producto.setEstado(EstadoProducto.Activo);

        return producto;
    }
}
