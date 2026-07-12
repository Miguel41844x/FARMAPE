package com.farmape.backend.ventas.service;

import com.farmape.backend.clientes.model.Cliente;
import com.farmape.backend.clientes.repository.ClienteRepository;
import com.farmape.backend.productos.enums.EstadoProducto;
import com.farmape.backend.productos.model.Producto;
import com.farmape.backend.productos.repository.ProductoRepository;
import com.farmape.backend.security.AuthenticatedUserService;
import com.farmape.backend.support.TestDataFactory;
import com.farmape.backend.ventas.dto.CrearOrdenVentaRequest;
import com.farmape.backend.ventas.dto.DetalleVentaRequest;
import com.farmape.backend.ventas.enums.CanalPedido;
import com.farmape.backend.ventas.enums.EstadoOrdenVenta;
import com.farmape.backend.ventas.model.OrdenVenta;
import com.farmape.backend.ventas.repository.DetalleOrdenVentaRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {
    @Mock OrdenVentaRepository ordenVentaRepository;
    @Mock DetalleOrdenVentaRepository detalleOrdenVentaRepository;
    @Mock HistorialOrdenVentaRepository historialOrdenVentaRepository;
    @Mock ClienteRepository clienteRepository;
    @Mock ProductoRepository productoRepository;
    @Mock AuthenticatedUserService authenticatedUserService;
    @InjectMocks VentaService ventaService;

    @Test
    void crearOrdenCalculaTotalConPrecioActualDelProducto() {
        Cliente cliente = Cliente.builder().idCliente(1).nombres("Ana").apellidos("Paz").build();
        Producto producto = Producto.builder().idProducto(2).nombre("Paracetamol")
                .precioVenta(new BigDecimal("2.50")).stockActual(20).estado(EstadoProducto.Activo).build();
        CrearOrdenVentaRequest request = new CrearOrdenVentaRequest(
                1, CanalPedido.Presencial, null, List.of(new DetalleVentaRequest(2, 3)));
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(2)).thenReturn(Optional.of(producto));
        when(authenticatedUserService.currentAccount()).thenReturn(TestDataFactory.cuentaAdministradorActiva());
        when(ordenVentaRepository.save(any())).thenAnswer(invocation -> {
            OrdenVenta orden = invocation.getArgument(0);
            orden.setIdOrdenVenta(11);
            return orden;
        });
        when(detalleOrdenVentaRepository.findByOrdenVenta(any())).thenReturn(List.of());

        var response = ventaService.crearOrden(request);

        assertThat(response.total()).isEqualByComparingTo("7.50");
        assertThat(response.estado()).isEqualTo(EstadoOrdenVenta.Pendiente);
    }

    @Test
    void crearOrdenRechazaStockInsuficiente() {
        Cliente cliente = Cliente.builder().idCliente(1).nombres("Ana").build();
        Producto producto = Producto.builder().idProducto(2).nombre("Producto")
                .precioVenta(BigDecimal.ONE).stockActual(1).estado(EstadoProducto.Activo).build();
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(2)).thenReturn(Optional.of(producto));
        when(authenticatedUserService.currentAccount()).thenReturn(TestDataFactory.cuentaAdministradorActiva());
        when(ordenVentaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        CrearOrdenVentaRequest request = new CrearOrdenVentaRequest(
                1, CanalPedido.Presencial, null, List.of(new DetalleVentaRequest(2, 5)));

        assertThatThrownBy(() -> ventaService.crearOrden(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Stock insuficiente");
    }
}
