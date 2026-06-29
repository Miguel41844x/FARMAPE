package com.farmape.backend.productos.service;

import com.farmape.backend.productos.dto.ProductoRequest;
import com.farmape.backend.productos.enums.EstadoProducto;
import com.farmape.backend.productos.model.Categoria;
import com.farmape.backend.productos.model.Producto;
import com.farmape.backend.productos.repository.CategoriaRepository;
import com.farmape.backend.productos.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {
    @Mock ProductoRepository productoRepository;
    @Mock CategoriaRepository categoriaRepository;
    @InjectMocks ProductoService productoService;

    @Test
    void crearAsignaEstadoActivoPorDefecto() {
        Categoria categoria = Categoria.builder().idCategoria(2).nombre("Analgésicos").build();
        ProductoRequest request = new ProductoRequest(2, "Paracetamol", null, "Lab",
                new BigDecimal("1.00"), new BigDecimal("2.50"), 10, 2, null, null);
        when(categoriaRepository.findById(2)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any())).thenAnswer(invocation -> {
            Producto producto = invocation.getArgument(0);
            producto.setIdProducto(5);
            return producto;
        });

        var response = productoService.crear(request);

        assertThat(response.idProducto()).isEqualTo(5);
        assertThat(response.estado()).isEqualTo(EstadoProducto.Activo);
        assertThat(response.precioVenta()).isEqualByComparingTo("2.50");
    }

    @Test
    void crearRechazaCategoriaInexistente() {
        ProductoRequest request = new ProductoRequest(99, "Producto", null, null,
                null, BigDecimal.ONE, 0, 0, null, null);
        when(categoriaRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.crear(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Categoría no encontrada");
    }
}
