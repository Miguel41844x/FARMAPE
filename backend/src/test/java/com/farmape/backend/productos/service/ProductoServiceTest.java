package com.farmape.backend.productos.service;

import com.farmape.backend.productos.dto.ProductoRequest;
import com.farmape.backend.productos.dto.ProductoResponse;
import com.farmape.backend.productos.enums.EstadoProducto;
import com.farmape.backend.productos.model.Categoria;
import com.farmape.backend.productos.model.Producto;
import com.farmape.backend.productos.repository.CategoriaRepository;
import com.farmape.backend.productos.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void crearAsignaEstadoActivoCuandoRequestNoTraeEstado() {
        Categoria categoria = Categoria.builder()
                .idCategoria(3)
                .nombre("Analgesicos")
                .build();
        ProductoRequest request = new ProductoRequest(
                3,
                "Paracetamol 500mg",
                "Caja x 100 tabletas",
                "FarmaLab",
                new BigDecimal("0.50"),
                new BigDecimal("1.20"),
                25,
                5,
                LocalDate.of(2027, 6, 30),
                null
        );

        when(categoriaRepository.findById(3)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> {
            Producto producto = invocation.getArgument(0);
            producto.setIdProducto(11);
            return producto;
        });

        ProductoResponse response = productoService.crear(request);

        assertThat(response.idProducto()).isEqualTo(11);
        assertThat(response.idCategoria()).isEqualTo(3);
        assertThat(response.categoria()).isEqualTo("Analgesicos");
        assertThat(response.nombre()).isEqualTo("Paracetamol 500mg");
        assertThat(response.estado()).isEqualTo(EstadoProducto.Activo);

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoProducto.Activo);
    }

    @Test
    void crearLanzaErrorCuandoCategoriaNoExiste() {
        ProductoRequest request = new ProductoRequest(
                99,
                "Ibuprofeno",
                null,
                null,
                null,
                new BigDecimal("2.00"),
                10,
                2,
                null,
                EstadoProducto.Activo
        );

        when(categoriaRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.crear(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no encontrada");

        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void cambiarEstadoActualizaProductoExistente() {
        Categoria categoria = Categoria.builder()
                .idCategoria(4)
                .nombre("Vitaminas")
                .build();
        Producto producto = Producto.builder()
                .idProducto(15)
                .categoria(categoria)
                .nombre("Vitamina C")
                .precioVenta(new BigDecimal("3.50"))
                .estado(EstadoProducto.Activo)
                .build();

        when(productoRepository.findById(15)).thenReturn(Optional.of(producto));
        when(productoRepository.save(producto)).thenReturn(producto);

        ProductoResponse response = productoService.cambiarEstado(15, EstadoProducto.Inactivo);

        assertThat(response.idProducto()).isEqualTo(15);
        assertThat(response.estado()).isEqualTo(EstadoProducto.Inactivo);
        verify(productoRepository).save(producto);
    }
}
