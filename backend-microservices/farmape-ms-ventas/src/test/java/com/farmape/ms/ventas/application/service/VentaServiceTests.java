package com.farmape.ms.ventas.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import com.farmape.ms.ventas.api.dto.ActualizarVentaRequest;
import com.farmape.ms.ventas.api.dto.CrearVentaRequest;
import com.farmape.ms.ventas.api.dto.DetalleVentaRequest;
import com.farmape.ms.ventas.application.client.InventarioClient;
import com.farmape.ms.ventas.application.client.InventarioProductoResponse;
import com.farmape.ms.ventas.application.exception.VentaBusinessException;
import com.farmape.ms.ventas.application.exception.VentaIntegrationException;
import com.farmape.ms.ventas.application.exception.VentaNotFoundException;
import com.farmape.ms.ventas.domain.model.CanalPedido;
import com.farmape.ms.ventas.domain.model.DetalleVenta;
import com.farmape.ms.ventas.domain.model.EstadoVenta;
import com.farmape.ms.ventas.domain.model.Venta;
import com.farmape.ms.ventas.domain.repository.VentaRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VentaServiceTests {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private VentaService ventaService;

    @Test
    void registrarVentaCreadaCorrectamente() {
        when(inventarioClient.obtenerProducto(1)).thenReturn(producto(1, "Paracetamol", "1.50", 10, "Activo"));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = ventaService.registrarVenta(requestProducto(1, 2));

        assertThat(response.estado()).isEqualTo(EstadoVenta.Pendiente);
        assertThat(response.canalPedido()).isEqualTo(CanalPedido.Presencial);
        assertThat(response.total()).isEqualByComparingTo("3.00");
        assertThat(response.detalles()).hasSize(1);
        verify(inventarioClient).reducirStock(1, 2, response.idOrdenVenta());
    }

    @Test
    void registrarVentaConVariosProductosYDuplicadosConsolidaCantidades() {
        when(inventarioClient.obtenerProducto(1)).thenReturn(producto(1, "Paracetamol", "1.00", 10, "Activo"));
        when(inventarioClient.obtenerProducto(2)).thenReturn(producto(2, "Ibuprofeno", "2.00", 10, "Activo"));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = ventaService.registrarVenta(new CrearVentaRequest(
                1,
                "Ana Perez",
                null,
                7,
                "Luis Torres",
                CanalPedido.WhatsApp,
                "Pedido por WhatsApp",
                List.of(
                        new DetalleVentaRequest(1, 1),
                        new DetalleVentaRequest(2, 2),
                        new DetalleVentaRequest(1, 3)
                ),
                null
        ));

        assertThat(response.detalles()).hasSize(2);
        assertThat(response.detalles().get(0).cantidad()).isEqualTo(4);
        assertThat(response.total()).isEqualByComparingTo("8.00");
        assertThat(response.idEmpleado()).isEqualTo(7);
        verify(inventarioClient).reducirStock(1, 4, response.idOrdenVenta());
        verify(inventarioClient).reducirStock(2, 2, response.idOrdenVenta());
    }

    @Test
    void registrarVentaFallaConProductoInexistente() {
        when(inventarioClient.obtenerProducto(9)).thenThrow(new VentaBusinessException("Producto no encontrado: 9"));

        assertThatThrownBy(() -> ventaService.registrarVenta(requestProducto(9, 1)))
                .isInstanceOf(VentaBusinessException.class)
                .hasMessageContaining("Producto no encontrado");

        verify(ventaRepository, never()).save(any());
    }

    @Test
    void registrarVentaFallaConProductoInactivo() {
        when(inventarioClient.obtenerProducto(1)).thenReturn(producto(1, "Paracetamol", "1.00", 10, "Inactivo"));

        assertThatThrownBy(() -> ventaService.registrarVenta(requestProducto(1, 1)))
                .isInstanceOf(VentaBusinessException.class)
                .hasMessageContaining("no esta activo");
    }

    @Test
    void registrarVentaFallaConStockInsuficiente() {
        when(inventarioClient.obtenerProducto(1)).thenReturn(producto(1, "Paracetamol", "1.00", 1, "Activo"));

        assertThatThrownBy(() -> ventaService.registrarVenta(requestProducto(1, 2)))
                .isInstanceOf(VentaBusinessException.class)
                .hasMessageContaining("Stock insuficiente");
    }

    @Test
    void registrarVentaFallaConCantidadCero() {
        assertThatThrownBy(() -> ventaService.registrarVenta(requestProducto(1, 0)))
                .isInstanceOf(VentaBusinessException.class)
                .hasMessageContaining("mayor que cero");
    }

    @Test
    void registrarVentaFallaConCantidadNegativa() {
        assertThatThrownBy(() -> ventaService.registrarVenta(requestProducto(1, -1)))
                .isInstanceOf(VentaBusinessException.class)
                .hasMessageContaining("mayor que cero");
    }

    @Test
    void registrarVentaFallaSinProductos() {
        assertThatThrownBy(() -> ventaService.registrarVenta(new CrearVentaRequest(
                1,
                "Ana Perez",
                null,
                1,
                "Empleado 1",
                CanalPedido.Presencial,
                null,
                List.of(),
                null
        )))
                .isInstanceOf(VentaBusinessException.class)
                .hasMessageContaining("al menos un producto");
    }

    @Test
    void obtenerVentaFallaCuandoNoExiste() {
        when(ventaRepository.findByIdOrdenVenta(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ventaService.obtenerVenta(999))
                .isInstanceOf(VentaNotFoundException.class)
                .hasMessageContaining("Venta no encontrada");
    }

    @Test
    void listarVentasPorCliente() {
        when(ventaRepository.findByIdClienteOrderByFechaOrdenDesc(1))
                .thenReturn(List.of(ventaPendiente(1)));

        var response = ventaService.listarVentasPorCliente(1);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).idCliente()).isEqualTo(1);
    }

    @Test
    void actualizarVentaPendienteAjustaStockYRecalcula() {
        Venta venta = ventaPendiente(1);
        when(ventaRepository.findByIdOrdenVenta(1)).thenReturn(Optional.of(venta));
        when(inventarioClient.obtenerProducto(2)).thenReturn(producto(2, "Ibuprofeno", "2.00", 10, "Activo"));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = ventaService.actualizarVenta(1, new ActualizarVentaRequest(
                1,
                "Ana Perez",
                null,
                1,
                "Empleado 1",
                CanalPedido.Telefono,
                "Actualizada",
                List.of(new DetalleVentaRequest(2, 2)),
                null
        ));

        assertThat(response.detalles()).hasSize(1);
        assertThat(response.detalles().get(0).idProducto()).isEqualTo(2);
        assertThat(response.total()).isEqualByComparingTo("4.00");
        assertThat(response.canalPedido()).isEqualTo(CanalPedido.Telefono);
        verify(inventarioClient).restaurarStock(1, 2, 1);
        verify(inventarioClient).reducirStock(2, 2, 1);
    }

    @Test
    void actualizarVentaConfirmadaNoEstaPermitido() {
        Venta venta = ventaPendiente(1);
        venta.setEstado(EstadoVenta.Confirmada);
        when(ventaRepository.findByIdOrdenVenta(1)).thenReturn(Optional.of(venta));

        assertThatThrownBy(() -> ventaService.actualizarVenta(1, new ActualizarVentaRequest(
                1,
                "Ana Perez",
                null,
                1,
                "Empleado 1",
                CanalPedido.Presencial,
                null,
                List.of(new DetalleVentaRequest(1, 1)),
                null
        )))
                .isInstanceOf(VentaBusinessException.class)
                .hasMessageContaining("pendientes");
    }

    @Test
    void completarVentaConfirmaCorrectamente() {
        when(ventaRepository.findByIdOrdenVenta(1)).thenReturn(Optional.of(ventaPendiente(1)));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = ventaService.completarVenta(1);

        assertThat(response.estado()).isEqualTo(EstadoVenta.Confirmada);
    }

    @Test
    void cancelarVentaAnulaCorrectamenteYRestauraStock() {
        when(ventaRepository.findByIdOrdenVenta(1)).thenReturn(Optional.of(ventaPendiente(1)));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = ventaService.cancelarVenta(1);

        assertThat(response.estado()).isEqualTo(EstadoVenta.Anulada);
        verify(inventarioClient).restaurarStock(1, 2, 1);
    }

    @Test
    void cancelarVentaNuevamenteNoEstaPermitido() {
        Venta venta = ventaPendiente(1);
        venta.setEstado(EstadoVenta.Anulada);
        when(ventaRepository.findByIdOrdenVenta(1)).thenReturn(Optional.of(venta));

        assertThatThrownBy(() -> ventaService.cancelarVenta(1))
                .isInstanceOf(VentaBusinessException.class)
                .hasMessageContaining("anulada");
    }

    @Test
    void registrarVentaFallaConErrorDeComunicacionConProductos() {
        when(inventarioClient.obtenerProducto(1))
                .thenThrow(new VentaIntegrationException("No se pudo comunicar con el microservicio de inventario."));

        assertThatThrownBy(() -> ventaService.registrarVenta(requestProducto(1, 1)))
                .isInstanceOf(VentaIntegrationException.class)
                .hasMessageContaining("inventario");
    }

    @Test
    void registrarVentaCompensaStockCuandoFallaMongo() {
        when(inventarioClient.obtenerProducto(1)).thenReturn(producto(1, "Paracetamol", "1.00", 10, "Activo"));
        when(ventaRepository.save(any(Venta.class)))
                .thenThrow(new DataAccessResourceFailureException("Mongo no disponible"));

        assertThatThrownBy(() -> ventaService.registrarVenta(requestProducto(1, 2)))
                .isInstanceOf(DataAccessResourceFailureException.class);

        ArgumentCaptor<Integer> idVentaCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(inventarioClient).reducirStock(eq(1), eq(2), idVentaCaptor.capture());
        verify(inventarioClient).restaurarStock(1, 2, idVentaCaptor.getValue());
    }

    @Test
    void registrarVentaCompensaStockCuandoFallaDescuentoParcial() {
        when(inventarioClient.obtenerProducto(1)).thenReturn(producto(1, "Paracetamol", "1.00", 10, "Activo"));
        when(inventarioClient.obtenerProducto(2)).thenReturn(producto(2, "Ibuprofeno", "2.00", 10, "Activo"));
        doNothing()
                .doThrow(new VentaIntegrationException("No se pudo actualizar el stock del producto 2."))
                .when(inventarioClient).reducirStock(any(Integer.class), any(Integer.class), any(Integer.class));

        assertThatThrownBy(() -> ventaService.registrarVenta(new CrearVentaRequest(
                1,
                "Ana Perez",
                null,
                1,
                "Empleado 1",
                CanalPedido.Presencial,
                null,
                List.of(new DetalleVentaRequest(1, 1), new DetalleVentaRequest(2, 1)),
                null
        )))
                .isInstanceOf(VentaIntegrationException.class);

        verify(ventaRepository, never()).save(any());
        verify(inventarioClient).restaurarStock(eq(1), eq(1), any(Integer.class));
    }

    private CrearVentaRequest requestProducto(Integer idProducto, Integer cantidad) {
        return new CrearVentaRequest(
                1,
                "Ana Perez",
                null,
                1,
                "Empleado 1",
                CanalPedido.Presencial,
                null,
                List.of(new DetalleVentaRequest(idProducto, cantidad)),
                null
        );
    }

    private InventarioProductoResponse producto(
            Integer idProducto,
            String nombre,
            String precioVenta,
            Integer stockActual,
            String estado
    ) {
        return new InventarioProductoResponse(
                idProducto,
                1,
                "Medicamentos",
                "SKU-" + idProducto,
                nombre,
                "Descripcion",
                "Laboratorio",
                new BigDecimal("0.50"),
                new BigDecimal(precioVenta),
                stockActual,
                2,
                LocalDate.of(2027, 1, 1),
                false,
                estado
        );
    }

    private Venta ventaPendiente(Integer idOrdenVenta) {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setIdDetalleVenta(1);
        detalle.setIdProducto(1);
        detalle.setProducto("Paracetamol");
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(new BigDecimal("1.00"));
        detalle.setSubtotal(new BigDecimal("2.00"));

        Venta venta = new Venta();
        venta.setIdOrdenVenta(idOrdenVenta);
        venta.setIdCliente(1);
        venta.setCliente("Ana Perez");
        venta.setIdEmpleado(1);
        venta.setEmpleado("Empleado 1");
        venta.setCanalPedido(CanalPedido.Presencial);
        venta.setEstado(EstadoVenta.Pendiente);
        venta.setFechaOrden(LocalDateTime.now());
        venta.setTotal(new BigDecimal("2.00"));
        venta.setObservacion("Prueba");
        venta.setDetalles(List.of(detalle));
        return venta;
    }
}
