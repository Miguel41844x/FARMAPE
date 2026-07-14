package com.farmape.ms.auth.security;

import com.farmape.ms.auth.usuarios.model.CuentaUsuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.expiration-minutes}")
    private Long expirationMinutes;

    @Value("${app.jwt.refresh-expiration-days}")
    private Long refreshExpirationDays;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateAccessToken(CuentaUsuario cuenta, String rol, List<String> permisos) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("farmape-auth-service")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationMinutes * 60))
                .subject(cuenta.getUsuario())
                .claim("usuario", cuenta.getUsuario())
                .claim("email", cuenta.getEmail())
                .claim("rol", rol)
                .claim("permisos", permisos)
                .claim("idCuenta", cuenta.getIdCuenta())
                .claim("idTrabajador", cuenta.getTrabajador().getIdTrabajador())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public String generateRefreshToken(CuentaUsuario cuenta) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("farmape-auth-service")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(refreshExpirationDays * 24 * 60 * 60))
                .subject(cuenta.getUsuario())
                .claim("idCuenta", cuenta.getIdCuenta())
                .claim("tipo", "REFRESH")
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}