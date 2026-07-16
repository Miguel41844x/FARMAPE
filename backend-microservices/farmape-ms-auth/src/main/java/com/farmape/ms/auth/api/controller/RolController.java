package com.farmape.ms.auth.api.controller;

import com.farmape.ms.auth.api.dto.*;
import com.farmape.ms.auth.application.service.RolService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public List<RolResponse> listarRoles(
            @RequestParam(defaultValue = "false") boolean incluirInactivos
    ) {
        return rolService.listarRoles(incluirInactivos);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RolResponse crear(@Valid @RequestBody RolRequest request) {
        return rolService.crear(request);
    }

    @PutMapping("/{idRol}")
    public RolResponse actualizar(
            @PathVariable Integer idRol,
            @Valid @RequestBody RolRequest request
    ) {
        return rolService.actualizar(idRol, request);
    }

    @PutMapping("/{idRol}/permisos")
    public RolResponse asignarPermisos(
            @PathVariable Integer idRol,
            @Valid @RequestBody AsignarPermisosRequest request
    ) {
        return rolService.asignarPermisos(idRol, request);
    }

    @PatchMapping("/{idRol}/estado")
    public RolResponse cambiarEstado(
            @PathVariable Integer idRol,
            @Valid @RequestBody EstadoRolRequest request
    ) {
        return rolService.cambiarEstado(idRol, request);
    }

    @DeleteMapping("/{idRol}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer idRol) {
        rolService.eliminar(idRol);
        return ResponseEntity.noContent().build();
    }
}
