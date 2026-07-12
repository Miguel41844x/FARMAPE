package com.farmape.ms.inventario.api.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.farmape.ms.inventario.api.dto.CategoriaResponse;
import com.farmape.ms.inventario.api.dto.OrdenTiendaResponse;
import com.farmape.ms.inventario.api.dto.ProductoResponse;
import com.farmape.ms.inventario.api.dto.RepartoDomicilioResponse;
import com.farmape.ms.inventario.api.dto.VerificacionProductoResponse;
import com.farmape.ms.inventario.api.error.ApiExceptionHandler;
import com.farmape.ms.inventario.application.service.DespachoOperativoService;
import com.farmape.ms.inventario.application.service.InventarioConsultaService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InventarioHttpCompatibilityTests {

    private MockMvc mockMvc;

    private InventarioConsultaService inventarioConsultaService;

    private DespachoOperativoService despachoOperativoService;

    @BeforeEach
    void setUp() {
        inventarioConsultaService = mock(InventarioConsultaService.class);
        despachoOperativoService = mock(DespachoOperativoService.class);

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new InventarioConsultaController(inventarioConsultaService),
                        new DespachoOperativoController(despachoOperativoService)
                )
                .setControllerAdvice(new ApiExceptionHandler())
                .setMessageConverters(new JacksonJsonHttpMessageConverter())
                .build();
    }

    @Test
    void productosActivosRespondeEnRutaLegacyDelFrontend() throws Exception {
        when(inventarioConsultaService.listarProductosActivos())
                .thenReturn(List.of(producto()));

        mockMvc.perform(get("/api/productos/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProducto").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Paracetamol 500mg"))
                .andExpect(jsonPath("$[0].stockActual").value(56));
    }

    @Test
    void categoriasRespondeEnRutaCanonicaDeInventario() throws Exception {
        when(inventarioConsultaService.listarCategoriasActivas())
                .thenReturn(List.of(new CategoriaResponse(1, "Analgesicos", "Medicamentos para dolor", true)));

        mockMvc.perform(get("/api/inventario/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCategoria").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Analgesicos"));
    }

    @Test
    void verificacionesRespondeEnRutaLegacyDeAlmacen() throws Exception {
        when(inventarioConsultaService.listarVerificacionesProductos())
                .thenReturn(List.of(new VerificacionProductoResponse(
                        1,
                        10,
                        1,
                        "Paracetamol 500mg",
                        50,
                        50,
                        "CONFORME",
                        "Recepcion conforme"
                )));

        mockMvc.perform(get("/api/almacen/verificaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idVerificacion").value(1))
                .andExpect(jsonPath("$[0].estado").value("CONFORME"));
    }

    @Test
    void despachoRespondeEnRutaLegacyDelFrontend() throws Exception {
        when(despachoOperativoService.listarOrdenesTienda())
                .thenReturn(List.of(new OrdenTiendaResponse(
                        101,
                        "Ana Torres",
                        LocalDateTime.of(2026, 7, 8, 10, 15),
                        new BigDecimal("84.50"),
                        "PAGADA"
                )));

        mockMvc.perform(get("/api/despacho/ordenes-tienda"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idOrdenVenta").value(101))
                .andExpect(jsonPath("$[0].cliente").value("Ana Torres"))
                .andExpect(jsonPath("$[0].estado").value("PAGADA"));
    }

    @Test
    void entregaRepartoRespondeEnRutaCanonicaDeInventario() throws Exception {
        when(despachoOperativoService.entregarReparto(6))
                .thenReturn(new RepartoDomicilioResponse(
                        6,
                        201,
                        "Jorge Ramirez",
                        "Av. Arequipa 1200, Lince",
                        "Pedro Diaz",
                        "ENTREGADO"
                ));

        mockMvc.perform(patch("/api/inventario/despacho/repartos/6/entregar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReparto").value(6))
                .andExpect(jsonPath("$.estado").value("ENTREGADO"));
    }

    private ProductoResponse producto() {
        return new ProductoResponse(
                1,
                1,
                "Analgesicos",
                "PAR-500",
                "Paracetamol 500mg",
                "Tabletas para dolor y fiebre",
                "Medifarma",
                new BigDecimal("0.35"),
                new BigDecimal("1.00"),
                56,
                30,
                LocalDate.of(2026, 10, 23),
                false,
                "Activo"
        );
    }
}
