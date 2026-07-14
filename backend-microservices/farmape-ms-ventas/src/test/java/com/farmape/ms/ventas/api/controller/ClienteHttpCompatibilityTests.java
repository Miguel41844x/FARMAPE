package com.farmape.ms.ventas.api.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.farmape.ms.ventas.api.dto.ClienteRequest;
import com.farmape.ms.ventas.api.dto.ClienteResponse;
import com.farmape.ms.ventas.application.service.ClienteService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ClienteHttpCompatibilityTests {

    @Mock
    private ClienteService clienteService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ClienteController(clienteService))
                .build();
    }

    @Test
    void listarClientesEndpointUsadoPorFrontendEstaDisponible() throws Exception {
        when(clienteService.listarClientes()).thenReturn(List.of(cliente()));

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCliente").value(2))
                .andExpect(jsonPath("$[0].dniRuc").value("76543210"));

        verify(clienteService).listarClientes();
    }

    @Test
    void buscarClientePorDocumentoEndpointUsadoPorFrontendEstaDisponible() throws Exception {
        when(clienteService.buscarPorDocumento("76543210")).thenReturn(cliente());

        mockMvc.perform(get("/api/clientes/documento/76543210"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCliente").value(2))
                .andExpect(jsonPath("$.nombres").value("Ana"));

        verify(clienteService).buscarPorDocumento("76543210");
    }

    @Test
    void registrarClienteEndpointUsadoPorFrontendEstaDisponible() throws Exception {
        when(clienteService.registrarCliente(any(ClienteRequest.class))).thenReturn(cliente());

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "dniRuc": "76543210",
                                  "tipoCliente": "Natural",
                                  "nombres": "Ana",
                                  "apellidos": "Perez",
                                  "telefono": "987654321",
                                  "whatsapp": "987654321",
                                  "direccion": "Av. Los Jardines 123",
                                  "email": "ana.perez@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCliente").value(2))
                .andExpect(jsonPath("$.dniRuc").value("76543210"));

        verify(clienteService).registrarCliente(any(ClienteRequest.class));
    }

    private ClienteResponse cliente() {
        return new ClienteResponse(
                2,
                "Natural",
                "76543210",
                "76543210",
                "Ana",
                "Perez",
                "987654321",
                "987654321",
                "Av. Los Jardines 123",
                "ana.perez@example.com",
                LocalDateTime.now()
        );
    }
}
