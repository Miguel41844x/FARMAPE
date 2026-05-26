package com.farmape.backend.usuarios.controller;

import com.farmape.backend.usuarios.dto.CambiarEstadoCuentaRequest;
import com.farmape.backend.usuarios.dto.CuentaUsuarioResponse;
import com.farmape.backend.usuarios.service.CuentaUsuarioService;
import com.farmape.backend.usuarios.dto.CrearUsuarioRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class CuentaUsuarioController {

    private final CuentaUsuarioService cuentaUsuarioService;

    public CuentaUsuarioController(CuentaUsuarioService cuentaUsuarioService) {
        this.cuentaUsuarioService = cuentaUsuarioService;
    }

    @GetMapping
    public List<CuentaUsuarioResponse> listarUsuarios() {
        return cuentaUsuarioService.listarUsuarios();
    }

    @GetMapping("/{id}")
    public CuentaUsuarioResponse obtenerPorId(@PathVariable Integer id) {
        return cuentaUsuarioService.obtenerPorId(id);
    }

    @PostMapping
    public CuentaUsuarioResponse crear(@Valid @RequestBody CrearUsuarioRequest request) {
        return cuentaUsuarioService.crear(request);
    }

    @PatchMapping("/{id}/estado")
    public CuentaUsuarioResponse cambiarEstado(
            @PathVariable Integer id,
            @Valid @RequestBody CambiarEstadoCuentaRequest request
    ) {
        return cuentaUsuarioService.cambiarEstado(id, request);
    }
}