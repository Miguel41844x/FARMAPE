package com.farmape.backend.despacho.service;

import com.farmape.backend.clientes.model.Cliente;
import com.farmape.backend.despacho.repository.DespachoRepository;
import com.farmape.backend.security.AuthenticatedUserService;
import com.farmape.backend.support.TestDataFactory;
import com.farmape.backend.ventas.enums.CanalPedido;
import com.farmape.backend.ventas.enums.EstadoOrdenVenta;
import com.farmape.backend.ventas.model.OrdenVenta;
import com.farmape.backend.ventas.repository.OrdenVentaRepository;
import com.farmape.backend.ventas.service.VentaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DespachoServiceTest {
    @Mock OrdenVentaRepository ordenVentaRepository;
    @Mock DespachoRepository despachoRepository;
    @Mock AuthenticatedUserService authenticatedUserService;
    @Mock VentaService ventaService;
    @InjectMocks DespachoService despachoService;

    private OrdenVenta orden(EstadoOrdenVenta estado) {
        Cliente cliente = Cliente.builder().idCliente(1).nombres("Ana").apellidos("Paz").direccion("Lima").build();
        return OrdenVenta.builder().idOrdenVenta(5).cliente(cliente).estado(estado)
                .canalPedido(CanalPedido.Presencial).fechaOrden(LocalDateTime.now())
                .total(BigDecimal.TEN).build();
    }

    @Test
    void entregarOrdenTiendaCambiaEstadoYRegistraDespacho() {
   
        OrdenVenta orden = orden(EstadoOrdenVenta.Pagada);
        when(ordenVentaRepository.findById(5)).thenReturn(Optional.of(orden));
        when(ordenVentaRepository.save(orden)).thenReturn(orden);
        when(authenticatedUserService.currentAccount()).thenReturn(TestDataFactory.cuentaAdministradorActiva());
        when(despachoRepository.findByOrdenVenta(orden)).thenReturn(Optional.empty());
        when(despachoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));


        var response = despachoService.entregarOrdenTienda(5);


        assertThat(orden.getEstado()).isEqualTo(EstadoOrdenVenta.Despachada);
        assertThat(response.estado()).isEqualTo("ENTREGADA");
    }

    @Test
    void entregarOrdenTiendaRechazaOrdenNoPagada() {

        when(ordenVentaRepository.findById(5)).thenReturn(Optional.of(orden(EstadoOrdenVenta.Confirmada)));


        assertThatThrownBy(() -> despachoService.entregarOrdenTienda(5))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("órdenes pagadas");
    }
}
