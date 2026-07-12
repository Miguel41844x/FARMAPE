package com.farmape.ms.inventario.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.farmape.ms.inventario.domain.model.DespachoOperativo;
import com.farmape.ms.inventario.domain.repository.DespachoOperativoRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DespachoOperativoServiceTests {

    @Mock
    private DespachoOperativoRepository despachoOperativoRepository;

    @InjectMocks
    private DespachoOperativoService despachoOperativoService;

    @Test
    void listarOrdenesTiendaMapeaDespachosLocales() {
        DespachoOperativo despacho = despachoLocal();

        when(despachoOperativoRepository.findByTipoDespachoOrderByIdOrdenVentaDesc("LOCAL"))
                .thenReturn(List.of(despacho));

        var response = despachoOperativoService.listarOrdenesTienda();

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().idOrdenVenta()).isEqualTo(101);
        assertThat(response.getFirst().cliente()).isEqualTo("Ana Torres");
        assertThat(response.getFirst().estado()).isEqualTo("PAGADA");
    }

    @Test
    void entregarRepartoMarcaEstadoEntregado() {
        DespachoOperativo despacho = despachoDomicilio();

        when(despachoOperativoRepository.findById(6)).thenReturn(Optional.of(despacho));
        when(despachoOperativoRepository.save(despacho)).thenReturn(despacho);

        var response = despachoOperativoService.entregarReparto(6);

        assertThat(despacho.getEstado()).isEqualTo("ENTREGADO");
        assertThat(despacho.getFechaEntrega()).isNotNull();
        assertThat(response.estado()).isEqualTo("ENTREGADO");
    }

    @Test
    void prepararRepartoDesdeOrdenDevuelveRepartoExistente() {
        DespachoOperativo despacho = despachoDomicilio();

        when(despachoOperativoRepository.findByTipoDespachoAndIdOrdenVenta("DOMICILIO", 201))
                .thenReturn(Optional.of(despacho));

        var response = despachoOperativoService.prepararRepartoDesdeOrden(201);

        assertThat(response.idReparto()).isEqualTo(6);
        assertThat(response.idOrdenVenta()).isEqualTo(201);
        assertThat(response.estado()).isEqualTo("PENDIENTE");
    }

    @Test
    void prepararRepartoDesdeOrdenCreaRepartoPendienteDesdeOrdenLocal() {
        DespachoOperativo ordenLocal = despachoLocal();

        when(despachoOperativoRepository.findByTipoDespachoAndIdOrdenVenta("DOMICILIO", 101))
                .thenReturn(Optional.empty());
        when(despachoOperativoRepository.findByTipoDespachoAndIdOrdenVenta("LOCAL", 101))
                .thenReturn(Optional.of(ordenLocal));
        when(despachoOperativoRepository.save(org.mockito.ArgumentMatchers.any(DespachoOperativo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = despachoOperativoService.prepararRepartoDesdeOrden(101);

        assertThat(response.idOrdenVenta()).isEqualTo(101);
        assertThat(response.cliente()).isEqualTo("Ana Torres");
        assertThat(response.direccion()).isEqualTo("Pendiente de direccion");
        assertThat(response.estado()).isEqualTo("PENDIENTE");
    }

    private DespachoOperativo despachoLocal() {
        DespachoOperativo despacho = new DespachoOperativo();
        despacho.setIdDespacho(1);
        despacho.setIdOrdenVenta(101);
        despacho.setCliente("Ana Torres");
        despacho.setFechaOrden(LocalDateTime.of(2026, 7, 8, 10, 15));
        despacho.setTotal(new BigDecimal("84.50"));
        despacho.setTipoDespacho("LOCAL");
        despacho.setEstado("PAGADA");
        return despacho;
    }

    private DespachoOperativo despachoDomicilio() {
        DespachoOperativo despacho = new DespachoOperativo();
        despacho.setIdDespacho(6);
        despacho.setIdOrdenVenta(201);
        despacho.setCliente("Jorge Ramirez");
        despacho.setFechaOrden(LocalDateTime.of(2026, 7, 8, 13, 10));
        despacho.setTotal(new BigDecimal("97.40"));
        despacho.setTipoDespacho("DOMICILIO");
        despacho.setDireccion("Av. Arequipa 1200, Lince");
        despacho.setRepartidor("Pedro Diaz");
        despacho.setEstado("PENDIENTE");
        return despacho;
    }
}
