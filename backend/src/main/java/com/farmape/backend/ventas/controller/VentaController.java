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

    @GetMapping("/ultimas")
    public List<OrdenVentaResponse> listarUltimas() {
        return ventaService.listarUltimas();
    }

    @GetMapping("/pendientes")
    public List<OrdenVentaResponse> listarPendientes() {
        return ventaService.listarPendientes();
    }

    @GetMapping("/confirmadas")
    public List<OrdenVentaResponse> listarConfirmadas() {
        return ventaService.listarConfirmadas();
    }

    @GetMapping("/{id}")
    public OrdenVentaResponse obtenerPorId(@PathVariable Integer id) {
        return ventaService.obtenerPorId(id);
    }

    @PatchMapping("/{id}/confirmar")
    public OrdenVentaResponse confirmar(@PathVariable Integer id) {
        return ventaService.confirmar(id);
    }

    @PatchMapping("/{id}/rechazar")
    public OrdenVentaResponse rechazar(@PathVariable Integer id) {
        return ventaService.rechazar(id);
    }

    @PatchMapping("/{id}/anular")
    public OrdenVentaResponse anular(@PathVariable Integer id) {
        return ventaService.anular(id);
    }
}
