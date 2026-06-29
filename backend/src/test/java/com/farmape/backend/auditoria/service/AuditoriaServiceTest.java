package com.farmape.backend.auditoria.service;

import com.farmape.backend.auditoria.dto.RegistrarAuditoriaRequest;
import com.farmape.backend.auditoria.model.AuditoriaEvento;
import com.farmape.backend.auditoria.repository.AuditoriaEventoRepository;
import com.farmape.backend.security.AuthenticatedUserService;
import com.farmape.backend.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditoriaServiceTest {
    @Mock AuditoriaEventoRepository auditoriaEventoRepository;
    @Mock AuthenticatedUserService authenticatedUserService;
    @InjectMocks AuditoriaService auditoriaService;

    @Test
    void registrarManualNormalizaAccionYSeveridad() {
        RegistrarAuditoriaRequest request = new RegistrarAuditoriaRequest(
                " Ventas ", "Orden", "15", " actualizar ", "Cambio manual",
                null, null, "alta", null);
        when(authenticatedUserService.currentAccount()).thenReturn(TestDataFactory.cuentaAdministradorActiva());
        when(auditoriaEventoRepository.save(any())).thenAnswer(invocation -> {
            AuditoriaEvento evento = invocation.getArgument(0);
            evento.setIdAuditoria(8L);
            return evento;
        });

        var response = auditoriaService.registrarManual(request, "127.0.0.1");

        assertThat(response.idAuditoria()).isEqualTo(8L);
        assertThat(response.accion()).isEqualTo("ACTUALIZAR");
        assertThat(response.severidad()).isEqualTo("ALTA");
        ArgumentCaptor<AuditoriaEvento> captor = ArgumentCaptor.forClass(AuditoriaEvento.class);
        verify(auditoriaEventoRepository).save(captor.capture());
        assertThat(captor.getValue().getIp()).isEqualTo("127.0.0.1");
    }
}
