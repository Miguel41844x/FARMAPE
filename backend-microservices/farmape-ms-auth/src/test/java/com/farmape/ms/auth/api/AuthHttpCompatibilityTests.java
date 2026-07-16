package com.farmape.ms.auth.api;

import com.farmape.ms.auth.api.controller.AuthController;
import com.farmape.ms.auth.api.dto.LoginResponse;
import com.farmape.ms.auth.api.dto.RefreshTokenResponse;
import com.farmape.ms.auth.api.dto.SolicitarRestablecimientoResponse;
import com.farmape.ms.auth.application.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class AuthHttpCompatibilityTests {

    private MockMvc mockMvc;

    private AuthService authService;

    @BeforeEach
    void setUp() {

        authService = mock(AuthService.class);


        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new AuthController(authService)
                )
                .setMessageConverters(
                        new JacksonJsonHttpMessageConverter()
                )
                .build();

    }

    @Test
    void loginEndpointEstaDisponible() throws Exception {


        when(authService.login(any()))
                .thenReturn(
                        new LoginResponse(
                                "access-token-test",
                                "refresh-token-test",
                                "usuario-test",
                                "ADMIN",
                                "Juan",
                                "Perez",
                                1,
                                1,
                                List.of("USUARIO_LEER")
                        )
                );


        mockMvc.perform(
                post("/api/auth/login")
                        .contentType("application/json")
                        .content("""
                                {
                                    "usuario": "test",
                                    "clave": "test"
                                }
                                """)
        )
        .andExpect(status().isOk());

    }

    @Test
    void refreshEndpointEstaDisponible() throws Exception {


        when(authService.refreshToken(any()))
                .thenReturn(
                        new RefreshTokenResponse(
                                "nuevo-access-token"
                        )
                );


        mockMvc.perform(
                post("/api/auth/refresh")
                        .contentType("application/json")
                        .content("""
                                {
                                    "refreshToken": "refresh-token-test"
                                }
                                """)
        )
        .andExpect(status().isOk());

    }

    @Test
    void solicitarRestablecimientoEndpointEstaDisponible() throws Exception {


        when(authService.solicitarRestablecimiento(any()))
                .thenReturn(
                        new SolicitarRestablecimientoResponse(
                                true,
                                1L,
                                "Pendiente",
                                LocalDateTime.now(),
                                "Solicitud registrada correctamente"
                        )
                );

        mockMvc.perform(
                post("/api/auth/solicitar-restablecimiento")
                        .contentType("application/json")
                        .content("""
                                {
                                    "usuarioOCorreo": "test@test.com"
                                }
                                """)
        )
        .andExpect(status().isOk());

    }

}