package com.farmape.backend.almacen.service;

import com.farmape.backend.almacen.dto.RegistrarIngresoAlmacenRequest;
import com.farmape.backend.almacen.model.LoteProducto;
import com.farmape.backend.almacen.model.MovimientoAlmacen;
import com.farmape.backend.almacen.repository.DetalleRecepcionCompraRepository;
import com.farmape.backend.almacen.repository.LoteProductoRepository;
import com.farmape.backend.almacen.repository.MovimientoAlmacenRepository;
import com.farmape.backend.productos.model.Producto;
import com.farmape.backend.productos.repository.ProductoRepository;
import com.farmape.backend.security.AuthenticatedUserService;
import com.farmape.backend.support.TestDataFactory;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlmacenServiceTest {
    @Mock DetalleRecepcionCompraRepository detalleRecepcionCompraRepository;
    @Mock LoteProductoRepository loteProductoRepository;
    @Mock MovimientoAlmacenRepository movimientoAlmacenRepository;
    @Mock ProductoRepository productoRepository;
    @Mock AuthenticatedUserService authenticatedUserService;
    @InjectMocks AlmacenService almacenService;

    @Test
    void registrarIngresoActualizaLoteStockYMovimiento() {
        Producto producto = Producto.builder().idProducto(2).nombre("Alcohol")
                .precioCompra(new BigDecimal("3.00")).stockActual(10).build();
        RegistrarIngresoAlmacenRequest request = new RegistrarIngresoAlmacenRequest(
                30, 2, 5, "L-001", LocalDate.of(2027, 1, 1), 4);
        when(productoRepository.findById(2)).thenReturn(Optional.of(producto));
        when(loteProductoRepository.findByProductoAndNumeroLote(producto, "L-001")).thenReturn(Optional.empty());
        when(loteProductoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(authenticatedUserService.currentAccount()).thenReturn(TestDataFactory.cuentaAdministradorActiva());

        var response = almacenService.registrarIngreso(request);

        assertThat(producto.getStockActual()).isEqualTo(15);
        assertThat(response.cantidad()).isEqualTo(5);
        ArgumentCaptor<LoteProducto> lote = ArgumentCaptor.forClass(LoteProducto.class);
        verify(loteProductoRepository).save(lote.capture());
        assertThat(lote.getValue().getStockDisponible()).isEqualTo(5);
        verify(movimientoAlmacenRepository).save(any(MovimientoAlmacen.class));
    }

    @Test
    void registrarIngresoRechazaProductoInexistente() {
        when(productoRepository.findById(99)).thenReturn(Optional.empty());
        RegistrarIngresoAlmacenRequest request = new RegistrarIngresoAlmacenRequest(null, 99, 1, null, null, null);

        assertThatThrownBy(() -> almacenService.registrarIngreso(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Producto no encontrado");
    }
}
