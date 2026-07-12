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
import com.farmape.ms.inventario.domain.model.LoteProducto;
import com.farmape.ms.inventario.domain.model.MotivoMovimiento;
import com.farmape.ms.inventario.domain.model.MovimientoAlmacen;
import com.farmape.ms.inventario.domain.model.Producto;
import com.farmape.ms.inventario.domain.model.TipoMovimiento;
import com.farmape.ms.inventario.domain.model.VerificacionAlmacen;
import com.farmape.ms.inventario.domain.repository.CategoriaRepository;
import com.farmape.ms.inventario.domain.repository.LoteProductoRepository;
import com.farmape.ms.inventario.domain.repository.MovimientoAlmacenRepository;
import com.farmape.ms.inventario.domain.repository.ProductoRepository;
import com.farmape.ms.inventario.domain.repository.VerificacionAlmacenRepository;

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

    @Mock
    private MovimientoAlmacenRepository movimientoAlmacenRepository;

    @Mock
    private VerificacionAlmacenRepository verificacionAlmacenRepository;

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

    @Test
    void listarMovimientosPorProductoMapsMovimientoToResponse() {
        Producto producto = producto();
        MovimientoAlmacen movimiento = new MovimientoAlmacen();
        movimiento.setIdMovimiento(7);
        movimiento.setProducto(producto);
        movimiento.setIdTrabajador(3);
        movimiento.setTipoMovimiento(TipoMovimiento.Salida);
        movimiento.setMotivo(MotivoMovimiento.Venta);
        movimiento.setCantidad(2);
        movimiento.setObservacion("Salida por venta registrada");

        when(productoRepository.existsById(1)).thenReturn(true);
        when(movimientoAlmacenRepository.findTop20ByProductoIdProductoOrderByFechaMovimientoDesc(1))
                .thenReturn(List.of(movimiento));

        var responses = inventarioConsultaService.listarMovimientosPorProducto(1);

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().idMovimiento()).isEqualTo(7);
        assertThat(responses.getFirst().idProducto()).isEqualTo(1);
        assertThat(responses.getFirst().producto()).isEqualTo("Paracetamol 500mg");
        assertThat(responses.getFirst().tipoMovimiento()).isEqualTo("Salida");
        assertThat(responses.getFirst().motivo()).isEqualTo("Venta");
    }

    @Test
    void obtenerResumenConsolidaIndicadoresDeInventario() {
        Producto producto = producto();
        LoteProducto lote = lote(producto);
        MovimientoAlmacen movimiento = new MovimientoAlmacen();
        movimiento.setIdMovimiento(7);
        movimiento.setProducto(producto);
        movimiento.setTipoMovimiento(TipoMovimiento.Entrada);
        movimiento.setMotivo(MotivoMovimiento.Compra);
        movimiento.setCantidad(3);
        movimiento.setIdTrabajador(3);

        when(productoRepository.countByEstado(EstadoProducto.Activo)).thenReturn(120L);
        when(productoRepository.findProductosConStockBajo()).thenReturn(List.of(producto));
        when(loteProductoRepository.countByFechaVencimientoBetweenAndStockDisponibleGreaterThan(
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                0
        )).thenReturn(4L);
        when(loteProductoRepository.findTop5ByStockDisponibleGreaterThanOrderByFechaVencimientoAsc(0))
                .thenReturn(List.of(lote));
        when(movimientoAlmacenRepository.countByTipoMovimientoAndFechaMovimientoBetween(
                org.mockito.ArgumentMatchers.eq(TipoMovimiento.Entrada),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(2L);
        when(movimientoAlmacenRepository.countByTipoMovimientoAndFechaMovimientoBetween(
                org.mockito.ArgumentMatchers.eq(TipoMovimiento.Salida),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(1L);
        when(movimientoAlmacenRepository.countByTipoMovimientoAndFechaMovimientoBetween(
                org.mockito.ArgumentMatchers.eq(TipoMovimiento.Ajuste),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(1L);
        when(movimientoAlmacenRepository.findTop20ByOrderByFechaMovimientoDesc())
                .thenReturn(List.of(movimiento));

        var response = inventarioConsultaService.obtenerResumen();

        assertThat(response.totalProductosActivos()).isEqualTo(120);
        assertThat(response.productosConStockBajo()).isEqualTo(1);
        assertThat(response.lotesPorVencer()).isEqualTo(4);
        assertThat(response.entradasHoy()).isEqualTo(2);
        assertThat(response.salidasHoy()).isEqualTo(1);
        assertThat(response.ajustesHoy()).isEqualTo(1);
        assertThat(response.proximosVencimientos()).hasSize(1);
        assertThat(response.ultimosMovimientos()).hasSize(1);
    }

    @Test
    void listarVerificacionesProductosMapsDomainToResponse() {
        Producto producto = producto();
        VerificacionAlmacen verificacion = verificacion(producto);

        when(verificacionAlmacenRepository.findAllByOrderByFechaVerificacionDescIdVerificacionDesc())
                .thenReturn(List.of(verificacion));

        var responses = inventarioConsultaService.listarVerificacionesProductos();

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().idVerificacion()).isEqualTo(5);
        assertThat(responses.getFirst().idPedidoCompra()).isEqualTo(30);
        assertThat(responses.getFirst().producto()).isEqualTo("Paracetamol 500mg");
        assertThat(responses.getFirst().cantidadPedida()).isEqualTo(12);
        assertThat(responses.getFirst().cantidadRecibida()).isEqualTo(10);
        assertThat(responses.getFirst().estado()).isEqualTo("OBSERVADO");
    }

    @Test
    void confirmarVerificacionActualizaEstado() {
        Producto producto = producto();
        VerificacionAlmacen verificacion = verificacion(producto);

        when(verificacionAlmacenRepository.findById(5)).thenReturn(Optional.of(verificacion));
        when(verificacionAlmacenRepository.save(verificacion)).thenReturn(verificacion);

        var response = inventarioConsultaService.confirmarVerificacionProducto(5);

        assertThat(verificacion.getEstado()).isEqualTo("CONFORME");
        assertThat(verificacion.getObservacion()).isEqualTo("CONFORME");
        assertThat(response.estado()).isEqualTo("CONFORME");
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

    private LoteProducto lote(Producto producto) {
        LoteProducto lote = new LoteProducto();
        lote.setIdLote(11);
        lote.setProducto(producto);
        lote.setNumeroLote("LOTE-RESUMEN");
        lote.setFechaVencimiento(LocalDate.now().plusDays(15));
        lote.setCostoUnitario(new BigDecimal("0.35"));
        lote.setStockDisponible(12);
        lote.setEstado("Disponible");
        return lote;
    }

    private VerificacionAlmacen verificacion(Producto producto) {
        VerificacionAlmacen verificacion = new VerificacionAlmacen();
        verificacion.setIdVerificacion(5);
        verificacion.setIdPedidoCompra(30);
        verificacion.setProducto(producto);
        verificacion.setCantidadPedida(12);
        verificacion.setCantidadRecibida(10);
        verificacion.setEstado("OBSERVADO");
        verificacion.setObservacion("Diferencia en recepcion");
        return verificacion;
    }
}
