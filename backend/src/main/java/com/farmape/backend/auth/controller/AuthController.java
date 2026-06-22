package com.farmape.backend.auth.controller;

import com.farmape.backend.auth.dto.LoginRequest;
import com.farmape.backend.auth.dto.LoginResponse;
import com.farmape.backend.auth.dto.SolicitarRestablecimientoRequest;
import com.farmape.backend.auth.dto.SolicitarRestablecimientoResponse;
import com.farmape.backend.auth.dto.SolicitudRestablecimientoResponse;
import com.farmape.backend.auth.service.AuthService;
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
