package com.farmape.ms.inventario.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.farmape.ms.inventario.api.dto.MovimientoAlmacenRequest;
import com.farmape.ms.inventario.application.exception.InventarioBusinessException;
import com.farmape.ms.inventario.domain.model.Categoria;
import com.farmape.ms.inventario.domain.model.EstadoProducto;
import com.farmape.ms.inventario.domain.model.LoteProducto;
import com.farmape.ms.inventario.domain.model.MovimientoAlmacen;
import com.farmape.ms.inventario.domain.model.Producto;
import com.farmape.ms.inventario.domain.repository.LoteProductoRepository;
import com.farmape.ms.inventario.domain.repository.MovimientoAlmacenRepository;
import com.farmape.ms.inventario.domain.repository.ProductoRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventarioMovimientoServiceTests {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private LoteProductoRepository loteProductoRepository;

    @Mock
    private MovimientoAlmacenRepository movimientoAlmacenRepository;

    @InjectMocks
    private InventarioMovimientoService inventarioMovimientoService;

    @Test
    void registrarEntradaSumaStockEnProductoYLote() {
        Producto producto = producto(20);
        LoteProducto lote = lote(producto, 8);
        MovimientoAlmacenRequest request = new MovimientoAlmacenRequest(
                1,
                10,
                3,
                "Entrada",
                "Compra",
                5,
                "MANUAL",
                77,
                "Entrada manual"
        );

        when(productoRepository.findByIdForUpdate(1)).thenReturn(Optional.of(producto));
        when(loteProductoRepository.findByIdForUpdate(10)).thenReturn(Optional.of(lote));
        when(movimientoAlmacenRepository.save(any(MovimientoAlmacen.class)))
                .thenAnswer(invocation -> {
                    MovimientoAlmacen movimiento = invocation.getArgument(0);
                    movimiento.setIdMovimiento(99);
                    return movimiento;
                });

        var response = inventarioMovimientoService.registrarMovimiento(request);

        assertThat(producto.getStockActual()).isEqualTo(25);
        assertThat(lote.getStockDisponible()).isEqualTo(13);
        assertThat(response.idMovimiento()).isEqualTo(99);
        assertThat(response.tipoMovimiento()).isEqualTo("Entrada");
        assertThat(response.motivo()).isEqualTo("Compra");
    }

    @Test
    void registrarSalidaFallaCuandoNoHayStockSuficiente() {
        Producto producto = producto(2);
        MovimientoAlmacenRequest request = new MovimientoAlmacenRequest(
                1,
                null,
                3,
                "Salida",
                "Venta",
                5,
                null,
                null,
                "Salida manual"
        );

        when(productoRepository.findByIdForUpdate(1)).thenReturn(Optional.of(producto));

        assertThatThrownBy(() -> inventarioMovimientoService.registrarMovimiento(request))
                .isInstanceOf(InventarioBusinessException.class)
                .hasMessageContaining("Stock insuficiente");
    }

    private Producto producto(Integer stockActual) {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1);
        categoria.setNombre("Analgesicos");

        Producto producto = new Producto();
        producto.setIdProducto(1);
        producto.setCategoria(categoria);
        producto.setNombre("Paracetamol 500mg");
        producto.setPrecioCompra(new BigDecimal("0.35"));
        producto.setPrecioVenta(new BigDecimal("1.00"));
        producto.setStockActual(stockActual);
        producto.setStockMinimo(5);
        producto.setFechaVencimiento(LocalDate.of(2027, 1, 1));
        producto.setRequiereReceta(false);
        producto.setEstado(EstadoProducto.Activo);
        return producto;
    }

    private LoteProducto lote(Producto producto, Integer stockDisponible) {
        LoteProducto lote = new LoteProducto();
        lote.setIdLote(10);
        lote.setProducto(producto);
        lote.setNumeroLote("LOTE-TEST");
        lote.setFechaVencimiento(LocalDate.of(2027, 1, 1));
        lote.setCostoUnitario(new BigDecimal("0.35"));
        lote.setStockDisponible(stockDisponible);
        lote.setEstado("Disponible");
        lote.setFechaIngreso(LocalDateTime.now());
        return lote;
    }
}
