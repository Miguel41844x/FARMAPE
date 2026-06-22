package com.farmape.backend.compras.controller;

import com.farmape.backend.compras.dto.CrearOrdenCompraRequest;
import com.farmape.backend.compras.dto.OrdenCompraResponse;
import com.farmape.backend.compras.service.ComprasService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes-compra")
public class OrdenCompraController {

    private final ComprasService comprasService;

    public OrdenCompraController(ComprasService comprasService) {
        this.comprasService = comprasService;
    }

    @GetMapping
    public List<OrdenCompraResponse> listar() {
        return comprasService.listarOrdenesCompra();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrdenCompraResponse crear(@Valid @RequestBody CrearOrdenCompraRequest request) {
        return comprasService.crearOrdenCompra(request);
    }
}
