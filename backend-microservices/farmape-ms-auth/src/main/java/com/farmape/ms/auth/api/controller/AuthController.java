package com.farmape.ms.auth.auth.controller;

import com.farmape.ms.auth.auth.dto.LoginRequest;
import com.farmape.ms.auth.auth.dto.LoginResponse;
import com.farmape.ms.auth.auth.dto.RefreshTokenRequest;
import com.farmape.ms.auth.auth.dto.RefreshTokenResponse;
import com.farmape.ms.auth.auth.dto.SolicitarRestablecimientoRequest;
import com.farmape.ms.auth.auth.dto.SolicitarRestablecimientoResponse;
import com.farmape.ms.auth.auth.dto.SolicitudRestablecimientoResponse;
import com.farmape.ms.auth.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
    
    @PostMapping("/solicitar-restablecimiento")
    public ResponseEntity<SolicitarRestablecimientoResponse> solicitarRestablecimiento(
            @Valid @RequestBody SolicitarRestablecimientoRequest request
    ) {
        return ResponseEntity.ok(authService.solicitarRestablecimiento(request));
    }

    @GetMapping("/solicitudes-restablecimiento")
    public List<SolicitudRestablecimientoResponse> listarSolicitudesRestablecimiento() {
        return authService.listarSolicitudesRestablecimiento();
    }
}
