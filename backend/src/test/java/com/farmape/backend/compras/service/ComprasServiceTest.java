package com.farmape.backend.compras.service;

import com.farmape.backend.compras.dto.CrearOrdenCompraRequest;
import com.farmape.backend.compras.dto.DetalleOrdenCompraRequest;
import com.farmape.backend.compras.model.OrdenCompra;
import com.farmape.backend.compras.model.Proveedor;
import com.farmape.backend.compras.repository.*;
import com.farmape.backend.productos.model.Producto;
import com.farmape.backend.productos.repository.ProductoRepository;
import com.farmape.backend.security.AuthenticatedUserService;
import com.farmape.backend.support.TestDataFactory;
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
class ComprasServiceTest {
    @Mock ProveedorRepository proveedorRepository;
    @Mock OrdenCompraRepository ordenCompraRepository;
    @Mock FacturaCompraRepository facturaCompraRepository;
    @Mock NotaCreditoRepository notaCreditoRepository;
    @Mock PagoProveedorRepository pagoProveedorRepository;
    @Mock ProductoRepository productoRepository;
    @Mock AuthenticatedUserService authenticatedUserService;
    @InjectMocks ComprasService comprasService;

    @Test
    void crearOrdenCompraCalculaTotalDeDetalles() {
        Proveedor proveedor = Proveedor.builder().idProveedor(1).ruc("20111111111")
                .razonSocial("Proveedor SAC").activo(true).build();
        Producto producto = Producto.builder().idProducto(2).nombre("Alcohol").build();
        CrearOrdenCompraRequest request = new CrearOrdenCompraRequest(1, null, null, null, null,
                List.of(new DetalleOrdenCompraRequest(2, 3, new BigDecimal("4.50"))));
        when(proveedorRepository.findById(1)).thenReturn(Optional.of(proveedor));
        when(productoRepository.findById(2)).thenReturn(Optional.of(producto));
        when(authenticatedUserService.currentAccount()).thenReturn(TestDataFactory.cuentaAdministradorActiva());
        when(ordenCompraRepository.findTopByOrderByIdOrdenCompraDesc()).thenReturn(Optional.empty());
        when(ordenCompraRepository.save(any())).thenAnswer(invocation -> {
            OrdenCompra orden = invocation.getArgument(0);
            orden.setIdOrdenCompra(7);
            return orden;
        });

        var response = comprasService.crearOrdenCompra(request);

        assertThat(response.total()).isEqualByComparingTo("13.50");
        assertThat(response.detalles()).hasSize(1);
    }

    @Test
    void crearOrdenCompraRechazaOrdenSinDetalles() {
        CrearOrdenCompraRequest request = new CrearOrdenCompraRequest(1, null, null, null, null, List.of());

        assertThatThrownBy(() -> comprasService.crearOrdenCompra(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("al menos un producto");
    }
}
