package com.farmape.backend.compras.controller;

import com.farmape.backend.compras.dto.NotaCreditoResponse;
import com.farmape.backend.compras.dto.RegistrarNotaCreditoRequest;
import com.farmape.backend.compras.service.ComprasService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notas-credito-proveedor")
public class NotaCreditoProveedorController {

    private final ComprasService comprasService;

    public NotaCreditoProveedorController(ComprasService comprasService) {
        this.comprasService = comprasService;
    }

    @GetMapping
    public List<NotaCreditoResponse> listar() {
        return comprasService.listarNotasCredito();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotaCreditoResponse crear(@Valid @RequestBody RegistrarNotaCreditoRequest request) {
        return comprasService.registrarNotaCredito(request);
    }
}
