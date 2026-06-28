package com.farmape.backend.auth.service;

import com.farmape.backend.auth.dto.SolicitarRestablecimientoRequest;
import com.farmape.backend.auth.model.SolicitudRestablecimientoClave;
import com.farmape.backend.auth.repository.SolicitudRestablecimientoClaveRepository;
import com.farmape.backend.usuarios.model.CuentaUsuario;
import com.farmape.backend.usuarios.repository.CuentaUsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtEncoder jwtEncoder;
    @Mock
    private CuentaUsuarioRepository cuentaUsuarioRepository;
    @Mock
    private SolicitudRestablecimientoClaveRepository solicitudRepository;
    @InjectMocks
    private AuthService authService;

    @Test
    void solicitarRestablecimientoNormalizaDatosYAsociaCuentaExistente() {
        CuentaUsuario cuenta = CuentaUsuario.builder().idCuenta(4).usuario("cajero01").build();
        when(cuentaUsuarioRepository.findByUsuarioOrEmail("cajero01", "cajero01"))
                .thenReturn(Optional.of(cuenta));
        when(solicitudRepository.save(any(SolicitudRestablecimientoClave.class)))
                .thenAnswer(invocation -> {
                    SolicitudRestablecimientoClave solicitud = invocation.getArgument(0);
                    solicitud.setIdSolicitud(12L);
                    return solicitud;
                });

        var response = authService.solicitarRestablecimiento(
                new SolicitarRestablecimientoRequest("  cajero01  ", "  Olvidé mi clave  ")
        );

        assertThat(response.success()).isTrue();
        assertThat(response.idSolicitud()).isEqualTo(12);
        assertThat(response.estado()).isEqualTo("Pendiente");

        ArgumentCaptor<SolicitudRestablecimientoClave> captor =
                ArgumentCaptor.forClass(SolicitudRestablecimientoClave.class);
        verify(solicitudRepository).save(captor.capture());
        assertThat(captor.getValue().getUsuarioOCorreo()).isEqualTo("cajero01");
        assertThat(captor.getValue().getMensaje()).isEqualTo("Olvidé mi clave");
        assertThat(captor.getValue().getCuentaUsuario()).isSameAs(cuenta);
        assertThat(captor.getValue().getFechaSolicitud()).isNotNull();
    }

    @Test
    void solicitarRestablecimientoPermiteUsuarioNoRegistradoYOmitirMensaje() {
        when(cuentaUsuarioRepository.findByUsuarioOrEmail("desconocido", "desconocido"))
                .thenReturn(Optional.empty());
        when(solicitudRepository.save(any(SolicitudRestablecimientoClave.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        authService.solicitarRestablecimiento(
                new SolicitarRestablecimientoRequest("desconocido", "   ")
        );

        ArgumentCaptor<SolicitudRestablecimientoClave> captor =
                ArgumentCaptor.forClass(SolicitudRestablecimientoClave.class);
        verify(solicitudRepository).save(captor.capture());
        assertThat(captor.getValue().getCuentaUsuario()).isNull();
        assertThat(captor.getValue().getMensaje()).isNull();
    }
}
