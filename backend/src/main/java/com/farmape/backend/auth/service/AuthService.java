package com.farmape.backend.auth.service;

import com.farmape.backend.auth.dto.LoginRequest;
import com.farmape.backend.auth.dto.LoginResponse;
import com.farmape.backend.auth.dto.SolicitarRestablecimientoRequest;
import com.farmape.backend.auth.dto.SolicitarRestablecimientoResponse;
import com.farmape.backend.auth.dto.SolicitudRestablecimientoResponse;
import com.farmape.backend.auth.model.SolicitudRestablecimientoClave;
import com.farmape.backend.auth.repository.SolicitudRestablecimientoClaveRepository;
import com.farmape.backend.usuarios.model.CuentaUsuario;
import com.farmape.backend.usuarios.repository.CuentaUsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final CuentaUsuarioRepository cuentaUsuarioRepository;
    private final SolicitudRestablecimientoClaveRepository solicitudRestablecimientoClaveRepository;

    @Value("${app.jwt.expiration-minutes}")
    private Long expirationMinutes;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtEncoder jwtEncoder,
                       CuentaUsuarioRepository cuentaUsuarioRepository,
                       SolicitudRestablecimientoClaveRepository solicitudRestablecimientoClaveRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.cuentaUsuarioRepository = cuentaUsuarioRepository;
        this.solicitudRestablecimientoClaveRepository = solicitudRestablecimientoClaveRepository;
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

    @Transactional
    public SolicitarRestablecimientoResponse solicitarRestablecimiento(SolicitarRestablecimientoRequest request) {
        String usuarioOCorreo = request.usuarioOCorreo().trim();
        CuentaUsuario cuenta = cuentaUsuarioRepository
                .findByUsuarioOrEmail(usuarioOCorreo, usuarioOCorreo)
                .orElse(null);

        SolicitudRestablecimientoClave solicitud = SolicitudRestablecimientoClave.builder()
                .usuarioOCorreo(usuarioOCorreo)
                .cuentaUsuario(cuenta)
                .mensaje(limpiar(request.mensaje()))
                .estado("Pendiente")
                .fechaSolicitud(LocalDateTime.now())
                .build();

        solicitud = solicitudRestablecimientoClaveRepository.save(solicitud);

        return new SolicitarRestablecimientoResponse(
                true,
                solicitud.getIdSolicitud(),
                solicitud.getEstado(),
                solicitud.getFechaSolicitud(),
                "Solicitud registrada. Comunícate con el administrador para validar tu identidad y restablecer el acceso."
        );
    }

    @Transactional(readOnly = true)
    public List<SolicitudRestablecimientoResponse> listarSolicitudesRestablecimiento() {
        return solicitudRestablecimientoClaveRepository.findTop50ByOrderByFechaSolicitudDesc().stream()
                .map(solicitud -> {
                    CuentaUsuario cuenta = solicitud.getCuentaUsuario();
                    return new SolicitudRestablecimientoResponse(
                            solicitud.getIdSolicitud(),
                            solicitud.getUsuarioOCorreo(),
                            cuenta != null ? cuenta.getIdCuenta() : null,
                            cuenta != null ? cuenta.getUsuario() : null,
                            cuenta != null ? cuenta.getEmail() : null,
                            solicitud.getMensaje(),
                            solicitud.getEstado(),
                            solicitud.getFechaSolicitud()
                    );
                })
                .toList();
    }

    private String limpiar(String valor) {
        if (valor == null) {
            return null;
        }
        String limpio = valor.trim();
        return limpio.isBlank() ? null : limpio;
    }
}
