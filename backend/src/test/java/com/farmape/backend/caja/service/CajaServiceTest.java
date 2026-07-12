package com.farmape.backend.caja.service;

import com.farmape.backend.almacen.repository.LoteProductoRepository;
import com.farmape.backend.caja.dto.RegistrarPagoRequest;
import com.farmape.backend.caja.enums.MetodoPago;
import com.farmape.backend.caja.enums.TipoComprobante;
import com.farmape.backend.caja.repository.ComprobanteVentaRepository;
import com.farmape.backend.caja.repository.PagoVentaRepository;
import com.farmape.backend.caja.repository.SerieComprobanteRepository;
import com.farmape.backend.productos.repository.ProductoRepository;
import com.farmape.backend.security.AuthenticatedUserService;
import com.farmape.backend.ventas.enums.EstadoOrdenVenta;
import com.farmape.backend.ventas.model.OrdenVenta;
import com.farmape.backend.ventas.repository.DetalleOrdenVentaRepository;
import com.farmape.backend.ventas.repository.DetalleVentaLoteRepository;
import com.farmape.backend.ventas.repository.OrdenVentaRepository;
import com.farmape.backend.ventas.service.VentaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CajaServiceTest {
    @Mock OrdenVentaRepository ordenVentaRepository;
    @Mock DetalleOrdenVentaRepository detalleOrdenVentaRepository;
    @Mock ProductoRepository productoRepository;
    @Mock LoteProductoRepository loteProductoRepository;
    @Mock DetalleVentaLoteRepository detalleVentaLoteRepository;
    @Mock PagoVentaRepository pagoVentaRepository;
    @Mock ComprobanteVentaRepository comprobanteVentaRepository;
    @Mock SerieComprobanteRepository serieComprobanteRepository;
    @Mock VentaService ventaService;
    @Mock AuthenticatedUserService authenticatedUserService;
    @InjectMocks CajaService cajaService;

    @Test
    void registrarPagoRechazaMontoMenorAlTotal() {
        OrdenVenta orden = OrdenVenta.builder().idOrdenVenta(1).estado(EstadoOrdenVenta.Confirmada)
                .total(new BigDecimal("50.00")).build();
        when(ordenVentaRepository.findByIdForUpdate(1)).thenReturn(Optional.of(orden));

        assertThatThrownBy(() -> cajaService.registrarPago(1,
                new RegistrarPagoRequest(new BigDecimal("40.00"), MetodoPago.Efectivo, TipoComprobante.Boleta)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no cubre");
    }

    @Test
    void registrarPagoEvitaPagoDuplicado() {
        OrdenVenta orden = OrdenVenta.builder().idOrdenVenta(1).estado(EstadoOrdenVenta.Confirmada)
                .total(new BigDecimal("50.00")).build();
        when(ordenVentaRepository.findByIdForUpdate(1)).thenReturn(Optional.of(orden));
        when(pagoVentaRepository.existsByOrdenVenta_IdOrdenVenta(1)).thenReturn(true);

        assertThatThrownBy(() -> cajaService.registrarPago(1,
                new RegistrarPagoRequest(new BigDecimal("50.00"), MetodoPago.Efectivo, TipoComprobante.Boleta)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ya tiene un pago");
    }
}
