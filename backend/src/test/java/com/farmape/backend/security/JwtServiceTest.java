package com.farmape.backend.security;

import com.farmape.backend.support.TestDataFactory;
import com.farmape.backend.usuarios.model.CuentaUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(jwtEncoder);
        ReflectionTestUtils.setField(jwtService, "expirationMinutes", 60L);
        ReflectionTestUtils.setField(jwtService, "refreshExpirationDays", 7L);
    }

    @Test
    void generateAccessTokenIncluyeClaimsDelUsuarioRolYPermisos() {
        CuentaUsuario cuenta = TestDataFactory.cuentaAdministradorActiva();
        Jwt jwtCodificado = encodedJwt("access-token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwtCodificado);

        String token = jwtService.generateAccessToken(
                cuenta, "Administrador", List.of("VENTAS_VER")
        );

        assertThat(token).isEqualTo("access-token");
        ArgumentCaptor<JwtEncoderParameters> captor =
                ArgumentCaptor.forClass(JwtEncoderParameters.class);
        org.mockito.Mockito.verify(jwtEncoder).encode(captor.capture());

        var claims = captor.getValue().getClaims();
        assertThat(claims.getClaimAsString("iss")).isEqualTo("farmape-backend");
        assertThat(claims.getSubject()).isEqualTo("admin");
        assertThat(claims.getClaimAsString("usuario")).isEqualTo("admin");
        assertThat(claims.getClaimAsString("email")).isEqualTo("admin@farmape.pe");
        assertThat(claims.getClaimAsString("rol")).isEqualTo("Administrador");
        assertThat(claims.getClaimAsStringList("permisos")).containsExactly("VENTAS_VER");
        assertThat(claims.<Integer>getClaim("idCuenta")).isEqualTo(10);
        assertThat(claims.<Integer>getClaim("idTrabajador")).isEqualTo(7);
        assertThat(Duration.between(claims.getIssuedAt(), claims.getExpiresAt()))
                .isEqualTo(Duration.ofMinutes(60));
    }

    @Test
    void generateRefreshTokenIncluyeTipoRefreshYVencimientoConfigurado() {
        CuentaUsuario cuenta = TestDataFactory.cuentaAdministradorActiva();
        Jwt jwtCodificado = encodedJwt("refresh-token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwtCodificado);

        String token = jwtService.generateRefreshToken(cuenta);

        assertThat(token).isEqualTo("refresh-token");
        ArgumentCaptor<JwtEncoderParameters> captor =
                ArgumentCaptor.forClass(JwtEncoderParameters.class);
        org.mockito.Mockito.verify(jwtEncoder).encode(captor.capture());

        var claims = captor.getValue().getClaims();
        assertThat(claims.getSubject()).isEqualTo("admin");
        assertThat(claims.getClaimAsString("tipo")).isEqualTo("REFRESH");
        assertThat(claims.<Integer>getClaim("idCuenta")).isEqualTo(10);
        assertThat(Duration.between(claims.getIssuedAt(), claims.getExpiresAt()))
                .isEqualTo(Duration.ofDays(7));
    }

    private Jwt encodedJwt(String tokenValue) {
        Instant now = Instant.now();
        return Jwt.withTokenValue(tokenValue)
                .header("alg", "HS256")
                .subject("admin")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .build();
    }
}
