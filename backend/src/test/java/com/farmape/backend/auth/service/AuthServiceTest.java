package com.farmape.backend.auth.service;

import com.farmape.backend.auth.dto.LoginRequest;
import com.farmape.backend.auth.dto.RefreshTokenRequest;
import com.farmape.backend.auth.dto.SolicitarRestablecimientoRequest;
import com.farmape.backend.auth.model.SolicitudRestablecimientoClave;
import com.farmape.backend.auth.repository.SolicitudRestablecimientoClaveRepository;
import com.farmape.backend.security.JwtService;
import com.farmape.backend.support.TestDataFactory;
import com.farmape.backend.usuarios.model.CuentaUsuario;
import com.farmape.backend.usuarios.repository.CuentaUsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private CuentaUsuarioRepository cuentaUsuarioRepository;

    @Mock
    private SolicitudRestablecimientoClaveRepository solicitudRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginRetornaTokensYPermisosActivosCuandoLasCredencialesSonValidas() {
        CuentaUsuario cuenta = TestDataFactory.cuentaAdministradorActiva();
        LoginRequest request = new LoginRequest("admin", "clave123");

        when(cuentaUsuarioRepository.findByUsuarioOrEmail("admin", "admin"))
                .thenReturn(Optional.of(cuenta));
        when(jwtService.generateAccessToken(cuenta, "Administrador", List.of("VENTAS_VER")))
                .thenReturn("access-token");
        when(jwtService.generateRefreshToken(cuenta)).thenReturn("refresh-token");

        var response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.usuario()).isEqualTo("admin");
        assertThat(response.rol()).isEqualTo("Administrador");
        assertThat(response.permisos()).containsExactly("VENTAS_VER");
        assertThat(cuenta.getUltimoAcceso()).isNotNull();

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authenticationCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authenticationCaptor.capture());
        assertThat(authenticationCaptor.getValue().getPrincipal()).isEqualTo("admin");
        assertThat(authenticationCaptor.getValue().getCredentials()).isEqualTo("clave123");
        verify(cuentaUsuarioRepository).save(cuenta);
    }

    @Test
    void loginPropagaErrorCuandoLasCredencialesSonInvalidas() {
        LoginRequest request = new LoginRequest("admin", "incorrecta");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Credenciales inválidas");

        verifyNoInteractions(jwtService);
        verify(cuentaUsuarioRepository, never()).save(any());
    }

    @Test
    void refreshTokenGeneraNuevoAccessTokenCuandoEsValido() {
        CuentaUsuario cuenta = TestDataFactory.cuentaAdministradorActiva();
        RefreshTokenRequest request = new RefreshTokenRequest("refresh-valido");
        Jwt jwt = refreshJwt("admin", "REFRESH");

        when(jwtDecoder.decode("refresh-valido")).thenReturn(jwt);
        when(cuentaUsuarioRepository.findByUsuario("admin")).thenReturn(Optional.of(cuenta));
        when(jwtService.generateAccessToken(cuenta, "Administrador", List.of("VENTAS_VER")))
                .thenReturn("nuevo-access-token");

        var response = authService.refreshToken(request);

        assertThat(response.accessToken()).isEqualTo("nuevo-access-token");
        verify(jwtService).generateAccessToken(cuenta, "Administrador", List.of("VENTAS_VER"));
    }

    @Test
    void refreshTokenRechazaTokenInvalidoOExpirado() {
        when(jwtDecoder.decode("refresh-invalido"))
                .thenThrow(new JwtException("Token inválido"));

        assertThatThrownBy(() -> authService.refreshToken(new RefreshTokenRequest("refresh-invalido")))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception -> {
                    assertThat(exception.getStatusCode().value()).isEqualTo(401);
                    assertThat(exception.getReason()).contains("inválido o expirado");
                });

        verifyNoInteractions(jwtService);
    }

    @Test
    void refreshTokenRechazaUnAccessToken() {
        when(jwtDecoder.decode("access-token")).thenReturn(refreshJwt("admin", "ACCESS"));

        assertThatThrownBy(() -> authService.refreshToken(new RefreshTokenRequest("access-token")))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception -> {
                    assertThat(exception.getStatusCode().value()).isEqualTo(401);
                    assertThat(exception.getReason()).contains("no es un token de refresco");
                });

        verify(cuentaUsuarioRepository, never()).findByUsuario(any());
    }

    @Test
    void refreshTokenRechazaCuentaInactiva() {
        CuentaUsuario cuenta = TestDataFactory.cuentaAdministradorActiva();
        cuenta.setEstado(com.farmape.backend.usuarios.enums.EstadoCuentaUsuario.Inactivo);
        when(jwtDecoder.decode("refresh-valido")).thenReturn(refreshJwt("admin", "REFRESH"));
        when(cuentaUsuarioRepository.findByUsuario("admin")).thenReturn(Optional.of(cuenta));

        assertThatThrownBy(() -> authService.refreshToken(new RefreshTokenRequest("refresh-valido")))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
                        assertThat(exception.getStatusCode().value()).isEqualTo(403));

        verifyNoInteractions(jwtService);
    }

    @Test
    void solicitarRestablecimientoNormalizaLosDatosSinAccederAMysql() {
        CuentaUsuario cuenta = TestDataFactory.cuentaAdministradorActiva();
        SolicitarRestablecimientoRequest request =
                new SolicitarRestablecimientoRequest("  admin  ", "  Olvidé mi clave  ");

        when(cuentaUsuarioRepository.findByUsuarioOrEmail("admin", "admin"))
                .thenReturn(Optional.of(cuenta));
        when(solicitudRepository.save(any(SolicitudRestablecimientoClave.class)))
                .thenAnswer(invocation -> {
                    SolicitudRestablecimientoClave solicitud = invocation.getArgument(0);
                    solicitud.setIdSolicitud(15L);
                    return solicitud;
                });

        var response = authService.solicitarRestablecimiento(request);

        assertThat(response.success()).isTrue();
        assertThat(response.idSolicitud()).isEqualTo(15L);
        assertThat(response.estado()).isEqualTo("Pendiente");

        ArgumentCaptor<SolicitudRestablecimientoClave> solicitudCaptor =
                ArgumentCaptor.forClass(SolicitudRestablecimientoClave.class);
        verify(solicitudRepository).save(solicitudCaptor.capture());
        assertThat(solicitudCaptor.getValue().getUsuarioOCorreo()).isEqualTo("admin");
        assertThat(solicitudCaptor.getValue().getMensaje()).isEqualTo("Olvidé mi clave");
        assertThat(solicitudCaptor.getValue().getCuentaUsuario()).isSameAs(cuenta);
        assertThat(solicitudCaptor.getValue().getFechaSolicitud()).isNotNull();
    }

    @Test
    void listarSolicitudesRestablecimientoMapeaSolicitudesConYSinCuentaAsociada() {
        CuentaUsuario cuenta = TestDataFactory.cuentaAdministradorActiva();
        LocalDateTime fecha = LocalDateTime.of(2026, 6, 28, 10, 30);
        SolicitudRestablecimientoClave asociada = SolicitudRestablecimientoClave.builder()
                .idSolicitud(20L)
                .usuarioOCorreo("admin")
                .cuentaUsuario(cuenta)
                .mensaje("No recuerdo mi clave")
                .estado("Pendiente")
                .fechaSolicitud(fecha)
                .build();
        SolicitudRestablecimientoClave sinCuenta = SolicitudRestablecimientoClave.builder()
                .idSolicitud(21L)
                .usuarioOCorreo("desconocido@correo.pe")
                .estado("Pendiente")
                .fechaSolicitud(fecha.plusMinutes(1))
                .build();
        when(solicitudRepository.findTop50ByOrderByFechaSolicitudDesc())
                .thenReturn(List.of(sinCuenta, asociada));

        var response = authService.listarSolicitudesRestablecimiento();

        assertThat(response).hasSize(2);
        assertThat(response.get(0).idCuenta()).isNull();
        assertThat(response.get(0).usuarioEncontrado()).isNull();
        assertThat(response.get(1).idCuenta()).isEqualTo(10);
        assertThat(response.get(1).usuarioEncontrado()).isEqualTo("admin");
        assertThat(response.get(1).emailEncontrado()).isEqualTo("admin@farmape.pe");
    }

    private Jwt refreshJwt(String subject, String tipo) {
        return Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .subject(subject)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .claim("tipo", tipo)
                .build();
    }
}
