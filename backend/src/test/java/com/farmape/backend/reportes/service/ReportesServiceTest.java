package com.farmape.backend.reportes.service;

import com.farmape.backend.reportes.dto.ActualizarEstadoAccionRequest;
import com.farmape.backend.security.AuthenticatedUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ReportesServiceTest {
    @Mock JdbcTemplate jdbcTemplate;
    @Mock AuthenticatedUserService authenticatedUserService;
    @InjectMocks ReportesService reportesService;

    @Test
    void actualizarEstadoAccionRechazaEstadoNoPermitidoAntesDeConsultarLaBase() {
        ActualizarEstadoAccionRequest request = new ActualizarEstadoAccionRequest("Desconocido");

        assertThatThrownBy(() -> reportesService.actualizarEstadoAccion(1, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Estado de acción no permitido");
        verifyNoInteractions(jdbcTemplate);
    }
}
