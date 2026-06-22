package com.farmape.backend.auth.service;

import com.farmape.backend.usuarios.model.CuentaUsuario;
import com.farmape.backend.usuarios.repository.CuentaUsuarioRepository;

import com.farmape.backend.auth.dto.LoginRequest;
import com.farmape.backend.auth.dto.LoginResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final CuentaUsuarioRepository cuentaUsuarioRepository;

    @Value("${app.jwt.expiration-minutes}")
    private Long expirationMinutes;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtEncoder jwtEncoder,
                       CuentaUsuarioRepository cuentaUsuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.cuentaUsuarioRepository = cuentaUsuarioRepository;
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.usuario(),
                        request.clave()
                )
        );

        CuentaUsuario cuenta = cuentaUsuarioRepository
                .findByUsuarioOrEmail(request.usuario(), request.usuario())
                .orElseThrow(() -> new RuntimeException("Usuario o email no encontrado"));

        cuenta.setUltimoAcceso(LocalDateTime.now());
        cuentaUsuarioRepository.save(cuenta);

        String rol = cuenta.getTrabajador().getRol().getNombreRol();
        List<String> permisos = cuenta.getTrabajador().getRol().getPermisos().stream()
                .filter(permiso -> Boolean.TRUE.equals(permiso.getActivo()))
                .map(permiso -> permiso.getCodigo())
                .sorted()
                .toList();

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("farmape-backend")
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

        String token = jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();

        return new LoginResponse(
                token,
                cuenta.getUsuario(),
                rol,
                cuenta.getTrabajador().getNombres(),
                cuenta.getTrabajador().getApellidos(),
                cuenta.getIdCuenta(),
                cuenta.getTrabajador().getIdTrabajador(),
                permisos
        );
    }
}
