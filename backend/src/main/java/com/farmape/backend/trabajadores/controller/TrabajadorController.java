package com.farmape.backend.trabajadores.controller;

import com.farmape.backend.trabajadores.dto.CambiarEstadoTrabajadorRequest;
import com.farmape.backend.trabajadores.dto.TrabajadorRequest;
import com.farmape.backend.trabajadores.dto.TrabajadorResponse;
import com.farmape.backend.trabajadores.service.TrabajadorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trabajadores")
public class TrabajadorController {

    private final TrabajadorService trabajadorService;

    public TrabajadorController(TrabajadorService trabajadorService) {
        this.trabajadorService = trabajadorService;
    }

    @GetMapping
    public List<TrabajadorResponse> listar() {
        return trabajadorService.listar();
    }

    @GetMapping("/{id}")
    public TrabajadorResponse obtenerPorId(@PathVariable Integer id) {
        return trabajadorService.obtenerPorId(id);
    }

    @PostMapping
    public TrabajadorResponse crear(@Valid @RequestBody TrabajadorRequest request) {
        return trabajadorService.crear(request);
    }

    @PutMapping("/{id}")
    public TrabajadorResponse actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody TrabajadorRequest request
    ) {
        return trabajadorService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public TrabajadorResponse cambiarEstado(
            @PathVariable Integer id,
            @Valid @RequestBody CambiarEstadoTrabajadorRequest request
    ) {
        return trabajadorService.cambiarEstado(id, request.estado());
    }
}