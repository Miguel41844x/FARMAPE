package com.farmape.ms.inventario.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.farmape.ms.inventario.api.dto.MovimientoAlmacenRequest;
import com.farmape.ms.inventario.api.dto.MovimientoAlmacenResponse;
import com.farmape.ms.inventario.application.service.InventarioMovimientoService;

@RestController
@RequestMapping("/api/inventario")
public class InventarioMovimientoController {

    private final InventarioMovimientoService inventarioMovimientoService;

    public InventarioMovimientoController(InventarioMovimientoService inventarioMovimientoService) {
        this.inventarioMovimientoService = inventarioMovimientoService;
    }

    @PostMapping("/movimientos")
    @ResponseStatus(HttpStatus.CREATED)
    public MovimientoAlmacenResponse registrarMovimiento(@RequestBody MovimientoAlmacenRequest request) {
        return inventarioMovimientoService.registrarMovimiento(request);
    }
}
