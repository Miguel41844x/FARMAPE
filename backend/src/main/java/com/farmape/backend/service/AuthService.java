package com.farmape.backend.service;

import com.farmape.backend.dto.auth.LoginRequest;
import com.farmape.backend.dto.auth.LoginResponse;
import com.farmape.backend.model.CuentaUsuario;
import com.farmape.backend.repository.CuentaUsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

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
        Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
                request.email(),
                request.clave()
        )
);

        CuentaUsuario cuenta = cuentaUsuarioRepository.findByEmail(request.email())
            .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        cuenta.setUltimoAcceso(LocalDateTime.now());
        cuentaUsuarioRepository.save(cuenta);

        String rol = cuenta.getTrabajador().getRol().getNombreRol();

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("farmape-backend")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationMinutes * 60))
                .subject(cuenta.getEmail())
                .claim("rol", rol)
                .claim("idCuenta", cuenta.getIdCuenta())
                .claim("idTrabajador", cuenta.getTrabajador().getIdTrabajador())
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new LoginResponse(
                token,
                cuenta.getEmail(),
                rol,
                cuenta.getTrabajador().getNombres(),
                cuenta.getTrabajador().getApellidos()
        );
    }
}