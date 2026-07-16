package com.farmape.ms.auth.security;

import com.farmape.ms.auth.domain.model.Rol;
import com.farmape.ms.auth.domain.model.EstadoTrabajador;
import com.farmape.ms.auth.domain.model.Trabajador;
import com.farmape.ms.auth.domain.model.EstadoCuentaUsuario;
import com.farmape.ms.auth.domain.model.CuentaUsuario;
import com.farmape.ms.auth.application.service.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtServiceTests {

    private JwtEncoder jwtEncoder;

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {

        jwtEncoder = mock(JwtEncoder.class);

        jwtService = new JwtService(jwtEncoder);

        setField("expirationMinutes", 60L);
        setField("refreshExpirationDays", 7L);

        Jwt jwt = mock(Jwt.class);

        when(jwt.getTokenValue())
                .thenReturn("token-generado");

        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(jwt);

    }

    @Test
    void generateAccessTokenDevuelveToken() {

        CuentaUsuario cuenta = crearCuenta();

        String token = jwtService.generateAccessToken(
                cuenta,
                "ADMIN",
                List.of("USER_MANAGE", "ROLE_READ")
        );

        assertThat(token)
                .isEqualTo("token-generado");

        verify(jwtEncoder)
                .encode(any(JwtEncoderParameters.class));

    }

    @Test
    void generateRefreshTokenDevuelveToken() {

        CuentaUsuario cuenta = crearCuenta();

        String token = jwtService.generateRefreshToken(cuenta);

        assertThat(token)
                .isEqualTo("token-generado");

        verify(jwtEncoder)
                .encode(any(JwtEncoderParameters.class));

    }

    private CuentaUsuario crearCuenta() {

        Rol rol = Rol.builder()
                .idRol(1)
                .nombreRol("ADMIN")
                .codigo("ADMIN")
                .permisos(Set.of())
                .build();

        Trabajador trabajador = Trabajador.builder()
                .idTrabajador(10)
                .rol(rol)
                .nombres("Juan")
                .apellidos("Perez")
                .estado(EstadoTrabajador.Activo)
                .build();

        return CuentaUsuario.builder()
                .idCuenta(20)
                .usuario("jperez")
                .email("jperez@test.com")
                .estado(EstadoCuentaUsuario.Activo)
                .trabajador(trabajador)
                .build();

    }

    private void setField(String nombre, Object valor) throws Exception {

        Field field = JwtService.class.getDeclaredField(nombre);

        field.setAccessible(true);

        field.set(jwtService, valor);

    }

}