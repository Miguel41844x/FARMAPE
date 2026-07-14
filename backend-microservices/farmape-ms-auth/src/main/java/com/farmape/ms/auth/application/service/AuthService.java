package com.farmape.ms.auth.auth.service;

import com.farmape.ms.auth.auth.dto.LoginRequest;
import com.farmape.ms.auth.auth.dto.LoginResponse;
import com.farmape.ms.auth.auth.dto.RefreshTokenRequest;
import com.farmape.ms.auth.auth.dto.RefreshTokenResponse;
import com.farmape.ms.auth.auth.dto.SolicitarRestablecimientoRequest;
import com.farmape.ms.auth.auth.dto.SolicitarRestablecimientoResponse;
import com.farmape.ms.auth.auth.dto.SolicitudRestablecimientoResponse;
import com.farmape.ms.auth.auth.model.SolicitudRestablecimientoClave;
import com.farmape.ms.auth.auth.repository.SolicitudRestablecimientoClaveRepository;
import com.farmape.ms.auth.trabajadores.enums.EstadoTrabajador;
import com.farmape.ms.auth.usuarios.enums.EstadoCuentaUsuario;
import com.farmape.ms.auth.usuarios.model.CuentaUsuario;
import com.farmape.ms.auth.usuarios.repository.CuentaUsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.server.ResponseStatusException;
import com.farmape.ms.auth.security.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtDecoder jwtDecoder;        
    private final CuentaUsuarioRepository cuentaUsuarioRepository;
    private final SolicitudRestablecimientoClaveRepository solicitudRestablecimientoClaveRepository;

    @Value("${app.jwt.expiration-minutes}")
    private Long expirationMinutes;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            JwtDecoder jwtDecoder,                                    
            CuentaUsuarioRepository cuentaUsuarioRepository,
            SolicitudRestablecimientoClaveRepository solicitudRestablecimientoClaveRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtDecoder = jwtDecoder;                               
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

        String accessToken = jwtService.generateAccessToken(cuenta, rol, permisos);
        String refreshToken = jwtService.generateRefreshToken(cuenta);

        return new LoginResponse(
                accessToken,
                refreshToken,
                cuenta.getUsuario(),
                rol,
                cuenta.getTrabajador().getNombres(),
                cuenta.getTrabajador().getApellidos(),
                cuenta.getIdCuenta(),
                cuenta.getTrabajador().getIdTrabajador(),
                permisos
        );
    }

  
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {

        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(request.refreshToken());
        } catch (JwtException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Token de refresco inválido o expirado"
            );
        }

        String tipo = jwt.getClaimAsString("tipo");
        if (!"REFRESH".equals(tipo)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "El token proporcionado no es un token de refresco"
            );
        }

        String username = jwt.getSubject();
        CuentaUsuario cuenta = cuentaUsuarioRepository
                .findByUsuario(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "La cuenta no existe"
                ));

        if (cuenta.getEstado() != EstadoCuentaUsuario.Activo) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La cuenta no está activa"
            );
        }

        if (cuenta.getTrabajador().getEstado() != EstadoTrabajador.Activo) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El trabajador no está activo"
            );
        }

        String rol = cuenta.getTrabajador().getRol().getNombreRol();
        List<String> permisos = cuenta.getTrabajador().getRol().getPermisos().stream()
                .filter(permiso -> Boolean.TRUE.equals(permiso.getActivo()))
                .map(permiso -> permiso.getCodigo())
                .sorted()
                .toList();

        String nuevoAccessToken = jwtService.generateAccessToken(cuenta, rol, permisos);

        return new RefreshTokenResponse(nuevoAccessToken);
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
        if (valor == null) return null;
        String limpio = valor.trim();
        return limpio.isBlank() ? null : limpio;
    }
}
