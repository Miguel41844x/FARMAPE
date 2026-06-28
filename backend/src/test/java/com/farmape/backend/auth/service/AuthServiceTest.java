package com.farmape.backend.auth.service;

import com.farmape.backend.auth.dto.LoginRequest;
import com.farmape.backend.auth.dto.SolicitarRestablecimientoRequest;
import com.farmape.backend.auth.model.SolicitudRestablecimientoClave;
import com.farmape.backend.auth.repository.SolicitudRestablecimientoClaveRepository;
import com.farmape.backend.roles.model.Permiso;
import com.farmape.backend.roles.model.Rol;
import com.farmape.backend.trabajadores.model.Trabajador;
import com.farmape.backend.usuarios.model.CuentaUsuario;
import com.farmape.backend.usuarios.repository.CuentaUsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void loginConCredencialesValidasRetornaDatosYToken() {
        // Arrange: datos y respuestas simuladas, igual al patrón mostrado en clase.
        Permiso permisoActivo = Permiso.builder()
                .idPermiso(1)
                .codigo("VENTAS_VER")
                .activo(true)
                .build();
        Permiso permisoInactivo = Permiso.builder()
                .idPermiso(2)
                .codigo("USUARIOS_EDITAR")
                .activo(false)
                .build();
        Rol rol = Rol.builder()
                .idRol(1)
                .nombreRol("Administrador")
                .permisos(new LinkedHashSet<>(List.of(permisoActivo, permisoInactivo)))
                .build();
        Trabajador trabajador = Trabajador.builder()
                .idTrabajador(7)
                .nombres("María")
                .apellidos("Torres")
                .rol(rol)
                .build();
        CuentaUsuario cuenta = CuentaUsuario.builder()
                .idCuenta(10)
                .usuario("admin")
                .email("admin@farmape.pe")
                .trabajador(trabajador)
                .build();
        LoginRequest request = new LoginRequest("admin", "clave123");
        Jwt jwt = Jwt.withTokenValue("token-de-prueba")
                .header("alg", "HS256")
                .claim("sub", "admin")
                .build();

        ReflectionTestUtils.setField(authService, "expirationMinutes", 60L);
        when(cuentaUsuarioRepository.findByUsuarioOrEmail("admin", "admin"))
                .thenReturn(Optional.of(cuenta));
        when(cuentaUsuarioRepository.save(cuenta)).thenReturn(cuenta);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // Act: se ejecuta el método real del servicio.
        var response = authService.login(request);

        // Assert: se comprueba el resultado y la interacción con las dependencias.
        assertNotNull(response);
        assertEquals("token-de-prueba", response.token());
        assertEquals("admin", response.usuario());
        assertEquals("Administrador", response.rol());
        assertEquals(List.of("VENTAS_VER"), response.permisos());
        assertNotNull(cuenta.getUltimoAcceso());

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authenticationCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authenticationCaptor.capture());
        assertEquals("admin", authenticationCaptor.getValue().getPrincipal());
        assertEquals("clave123", authenticationCaptor.getValue().getCredentials());
        verify(cuentaUsuarioRepository).save(cuenta);
    }

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
