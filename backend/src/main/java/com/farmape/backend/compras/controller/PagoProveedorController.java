package com.farmape.backend.compras.controller;

import com.farmape.backend.compras.dto.PagoProveedorResponse;
import com.farmape.backend.compras.dto.RegistrarPagoProveedorRequest;
import com.farmape.backend.compras.service.ComprasService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos-proveedor")
public class PagoProveedorController {

    private final ComprasService comprasService;

    public PagoProveedorController(ComprasService comprasService) {
        this.comprasService = comprasService;
    }

    @GetMapping
    public List<PagoProveedorResponse> listar() {
        return comprasService.listarPagosProveedor();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PagoProveedorResponse crear(@Valid @RequestBody RegistrarPagoProveedorRequest request) {
        return comprasService.registrarPagoProveedor(request);
    }
}
