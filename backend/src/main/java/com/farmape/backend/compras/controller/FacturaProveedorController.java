package com.farmape.backend.compras.controller;

import com.farmape.backend.compras.dto.FacturaProveedorResponse;
import com.farmape.backend.compras.dto.RegistrarFacturaProveedorRequest;
import com.farmape.backend.compras.service.ComprasService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturas-proveedor")
public class FacturaProveedorController {

    private final ComprasService comprasService;

    public FacturaProveedorController(ComprasService comprasService) {
        this.comprasService = comprasService;
    }

    @GetMapping
    public List<FacturaProveedorResponse> listar() {
        return comprasService.listarFacturasProveedor();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacturaProveedorResponse crear(@Valid @RequestBody RegistrarFacturaProveedorRequest request) {
        return comprasService.registrarFacturaProveedor(request);
    }
}
