package com.farmape.backend.formulas.service;

import com.farmape.backend.almacen.repository.LoteProductoRepository;
import com.farmape.backend.clientes.repository.ClienteRepository;
import com.farmape.backend.formulas.dto.CambiarEstadoRecetaRequest;
import com.farmape.backend.formulas.model.RecetaMagistral;
import com.farmape.backend.formulas.repository.DetalleFormulaMagistralRepository;
import com.farmape.backend.formulas.repository.FormulaMagistralRepository;
import com.farmape.backend.formulas.repository.RecetaMagistralRepository;
import com.farmape.backend.productos.enums.EstadoProducto;
import com.farmape.backend.productos.model.Producto;
import com.farmape.backend.productos.repository.ProductoRepository;
import com.farmape.backend.security.AuthenticatedUserService;
import com.farmape.backend.ventas.repository.HistorialOrdenVentaRepository;
import com.farmape.backend.ventas.repository.OrdenVentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FormulasServiceTest {
    @Mock RecetaMagistralRepository recetaMagistralRepository;
    @Mock FormulaMagistralRepository formulaMagistralRepository;
    @Mock DetalleFormulaMagistralRepository detalleFormulaMagistralRepository;
    @Mock ClienteRepository clienteRepository;
    @Mock ProductoRepository productoRepository;
    @Mock LoteProductoRepository loteProductoRepository;
    @Mock OrdenVentaRepository ordenVentaRepository;
    @Mock HistorialOrdenVentaRepository historialOrdenVentaRepository;
    @Mock AuthenticatedUserService authenticatedUserService;
    @InjectMocks FormulasService formulasService;

    @Test
    void listarInsumosDisponiblesSoloIncluyeActivosConStock() {
        Producto disponible = Producto.builder().idProducto(1).nombre("Base")
                .precioCompra(BigDecimal.ONE).precioVenta(BigDecimal.TEN)
                .stockActual(5).estado(EstadoProducto.Activo).build();
        Producto agotado = Producto.builder().idProducto(2).nombre("Agotado")
                .stockActual(0).estado(EstadoProducto.Activo).build();
        when(productoRepository.findByEstado(EstadoProducto.Activo))
                .thenReturn(List.of(disponible, agotado));

        var response = formulasService.listarInsumosDisponibles();

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().idProducto()).isEqualTo(1);
    }

    @Test
    void cambiarEstadoRechazaEstadoNoPermitido() {
        RecetaMagistral receta = RecetaMagistral.builder().idReceta(3).estado("Registrada").build();
        when(recetaMagistralRepository.findById(3)).thenReturn(Optional.of(receta));

        assertThatThrownBy(() -> formulasService.cambiarEstado(
                3, new CambiarEstadoRecetaRequest("Desconocido", null)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no permitido");
    }
}
