package com.farmape.ms.ventas.api.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.farmape.ms.ventas.api.dto.VentaResponse;
import com.farmape.ms.ventas.application.service.VentaService;
import com.farmape.ms.ventas.domain.model.CanalPedido;
import com.farmape.ms.ventas.domain.model.EstadoVenta;

@ExtendWith(MockitoExtension.class)
class VentaHttpCompatibilityTests {

    @Mock
    private VentaService ventaService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new VentaController(ventaService))
                .build();
    }

    @Test
    void confirmarEndpointUsadoPorFrontendEstaDisponible() throws Exception {
        when(ventaService.completarVenta(7)).thenReturn(venta(7, EstadoVenta.Confirmada));

        mockMvc.perform(patch("/api/ventas/7/confirmar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOrdenVenta").value(7))
                .andExpect(jsonPath("$.estado").value("Confirmada"));

        verify(ventaService).completarVenta(7);
    }

    @Test
    void rechazarEndpointUsadoPorFrontendEstaDisponible() throws Exception {
        when(ventaService.rechazarVenta(8)).thenReturn(venta(8, EstadoVenta.Rechazada));

        mockMvc.perform(patch("/api/ventas/8/rechazar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idOrdenVenta").value(8))
                .andExpect(jsonPath("$.estado").value("Rechazada"));

        verify(ventaService).rechazarVenta(8);
    }

    @Test
    void ultimasEndpointUsadoPorFrontendEstaDisponible() throws Exception {
        when(ventaService.listarUltimasVentas()).thenReturn(List.of(
                venta(3, EstadoVenta.Pendiente),
                venta(2, EstadoVenta.Confirmada)
        ));

        mockMvc.perform(get("/api/ventas/ultimas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idOrdenVenta").value(3))
                .andExpect(jsonPath("$[1].idOrdenVenta").value(2));

        verify(ventaService).listarUltimasVentas();
    }

    private VentaResponse venta(Integer idOrdenVenta, EstadoVenta estado) {
        return new VentaResponse(
                idOrdenVenta,
                1,
                "Cliente Demo",
                1,
                "Empleado Demo",
                CanalPedido.Presencial,
                estado,
                LocalDateTime.now(),
                new BigDecimal("10.00"),
                null,
                List.of()
        );
    }
}
