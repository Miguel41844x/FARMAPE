package com.farmape.ms.auth.perfil.controller;

import com.farmape.ms.auth.perfil.dto.ActualizarPerfilRequest;
import com.farmape.ms.auth.perfil.dto.PerfilUsuarioResponse;
import com.farmape.ms.auth.perfil.service.PerfilUsuarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/perfil")
public class PerfilUsuarioController {

    private final PerfilUsuarioService perfilUsuarioService;

    public PerfilUsuarioController(PerfilUsuarioService perfilUsuarioService) {
        this.perfilUsuarioService = perfilUsuarioService;
    }

    @GetMapping
    public PerfilUsuarioResponse obtenerPerfilActual() {
        return perfilUsuarioService.obtenerPerfilActual();
    }

    @PutMapping
    public PerfilUsuarioResponse actualizarPerfilActual(@Valid @RequestBody ActualizarPerfilRequest request) {
        return perfilUsuarioService.actualizarPerfilActual(request);
    }
}
