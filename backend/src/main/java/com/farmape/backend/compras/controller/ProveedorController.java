package com.farmape.backend.compras.controller;

import com.farmape.backend.compras.dto.ProveedorRequest;
import com.farmape.backend.compras.dto.ProveedorResponse;
import com.farmape.backend.compras.service.ComprasService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ComprasService comprasService;

    public ProveedorController(ComprasService comprasService) {
        this.comprasService = comprasService;
    }

    @GetMapping
    public List<ProveedorResponse> listar() {
        return comprasService.listarProveedores();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProveedorResponse crear(@Valid @RequestBody ProveedorRequest request) {
        return comprasService.crearProveedor(request);
    }

    @PutMapping("/{idProveedor}")
    public ProveedorResponse actualizar(@PathVariable Integer idProveedor,
                                        @Valid @RequestBody ProveedorRequest request) {
        return comprasService.actualizarProveedor(idProveedor, request);
    }

    @DeleteMapping("/{idProveedor}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer idProveedor) {
        comprasService.eliminarProveedor(idProveedor);
    }
}
