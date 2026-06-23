package com.farmape.backend.compras.service;

import com.farmape.backend.compras.dto.CrearOrdenCompraRequest;
import com.farmape.backend.compras.dto.DetalleOrdenCompraRequest;
import com.farmape.backend.compras.dto.OrdenCompraResponse;
import com.farmape.backend.compras.dto.ProveedorRequest;
import com.farmape.backend.compras.dto.ProveedorResponse;
import com.farmape.backend.compras.model.OrdenCompra;
import com.farmape.backend.compras.model.Proveedor;
import com.farmape.backend.compras.repository.FacturaCompraRepository;
import com.farmape.backend.compras.repository.NotaCreditoRepository;
import com.farmape.backend.compras.repository.OrdenCompraRepository;
import com.farmape.backend.compras.repository.PagoProveedorRepository;
import com.farmape.backend.compras.repository.ProveedorRepository;
import com.farmape.backend.productos.model.Producto;
import com.farmape.backend.productos.repository.ProductoRepository;
import com.farmape.backend.security.AuthenticatedUserService;
import com.farmape.backend.trabajadores.model.Trabajador;
import com.farmape.backend.usuarios.model.CuentaUsuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComprasServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @Mock
    private OrdenCompraRepository ordenCompraRepository;

    @Mock
    private FacturaCompraRepository facturaCompraRepository;

    @Mock
    private NotaCreditoRepository notaCreditoRepository;

    @Mock
    private PagoProveedorRepository pagoProveedorRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private ComprasService comprasService;

    @Test
    void crearProveedorLimpiaTextoYActivaPorDefecto() {
        ProveedorRequest request = new ProveedorRequest(
                " 20123456789 ",
                " Distribuidora Salud SAC ",
                " 999888777 ",
                " ventas@salud.com ",
                " Av. Industrial 100 ",
                null,
                null
        );

        when(proveedorRepository.existsByRuc("20123456789")).thenReturn(false);
        when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(invocation -> {
            Proveedor proveedor = invocation.getArgument(0);
            proveedor.setIdProveedor(4);
            return proveedor;
        });

        ProveedorResponse response = comprasService.crearProveedor(request);

        assertThat(response.idProveedor()).isEqualTo(4);
        assertThat(response.ruc()).isEqualTo("20123456789");
        assertThat(response.razonSocial()).isEqualTo("Distribuidora Salud SAC");
        assertThat(response.tipoProveedor()).isEqualTo("Proveedor");
        assertThat(response.activo()).isTrue();
    }

    @Test
    void crearOrdenCompraCalculaTotalYDetalle() {
        Proveedor proveedor = Proveedor.builder()
                .idProveedor(2)
                .ruc("20111111111")
                .razonSocial("Proveedor Medicinas")
                .tipoProveedor("Proveedor")
                .activo(true)
                .build();
        Producto producto = Producto.builder()
                .idProducto(5)
                .nombre("Alcohol medicinal")
                .build();
        Trabajador trabajador = Trabajador.builder()
                .idTrabajador(1)
                .nombres("Admin")
                .build();
        CuentaUsuario cuenta = CuentaUsuario.builder()
                .idCuenta(1)
                .trabajador(trabajador)
                .build();
        CrearOrdenCompraRequest request = new CrearOrdenCompraRequest(
                2,
                LocalDate.of(2026, 7, 10),
                "",
                "Entrega urgente",
                null,
                List.of(new DetalleOrdenCompraRequest(5, 3, new BigDecimal("12.50")))
        );

        when(proveedorRepository.findById(2)).thenReturn(Optional.of(proveedor));
        when(authenticatedUserService.currentAccount()).thenReturn(cuenta);
        when(ordenCompraRepository.findTopByOrderByIdOrdenCompraDesc()).thenReturn(Optional.empty());
        when(productoRepository.findById(5)).thenReturn(Optional.of(producto));
        when(ordenCompraRepository.save(any(OrdenCompra.class))).thenAnswer(invocation -> {
            OrdenCompra orden = invocation.getArgument(0);
            orden.setIdOrdenCompra(9);
            return orden;
        });

        OrdenCompraResponse response = comprasService.crearOrdenCompra(request);

        assertThat(response.idOrdenCompra()).isEqualTo(9);
        assertThat(response.numeroOrden()).isEqualTo("OC-000001");
        assertThat(response.medioPedido()).isEqualTo("Web");
        assertThat(response.total()).isEqualByComparingTo("37.50");
        assertThat(response.detalles()).hasSize(1);
        assertThat(response.detalles().get(0).subtotal()).isEqualByComparingTo("37.50");

        ArgumentCaptor<OrdenCompra> captor = ArgumentCaptor.forClass(OrdenCompra.class);
        verify(ordenCompraRepository).save(captor.capture());
        assertThat(captor.getValue().getDetalles().get(0).getOrdenCompra()).isSameAs(captor.getValue());
    }

    @Test
    void crearOrdenCompraLanzaErrorCuandoNoTieneDetalles() {
        CrearOrdenCompraRequest request = new CrearOrdenCompraRequest(
                2,
                null,
                "Web",
                null,
                null,
                List.of()
        );

        assertThatThrownBy(() -> comprasService.crearOrdenCompra(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("al menos un producto");

        verify(ordenCompraRepository, never()).save(any(OrdenCompra.class));
    }
}
