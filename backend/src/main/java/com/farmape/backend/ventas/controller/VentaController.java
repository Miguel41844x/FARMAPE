package com.farmape.backend.ventas.controller;

import com.farmape.backend.ventas.dto.CrearOrdenVentaRequest;
import com.farmape.backend.ventas.dto.OrdenVentaResponse;
import com.farmape.backend.ventas.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    public OrdenVentaResponse crearOrden(@Valid @RequestBody CrearOrdenVentaRequest request) {
        return ventaService.crearOrden(request);
    }

    @GetMapping
    public List<OrdenVentaResponse> listar() {
        return ventaService.listar();
    }

    @GetMapping("/pendientes")
    public List<OrdenVentaResponse> listarPendientes() {
        return ventaService.listarPendientes();
    }

    @GetMapping("/{id}")
    public OrdenVentaResponse obtenerPorId(@PathVariable Integer id) {
        return ventaService.obtenerPorId(id);
    }

    @PatchMapping("/{id}/anular")
    public OrdenVentaResponse anular(@PathVariable Integer id) {
        return ventaService.anular(id);
    }
}