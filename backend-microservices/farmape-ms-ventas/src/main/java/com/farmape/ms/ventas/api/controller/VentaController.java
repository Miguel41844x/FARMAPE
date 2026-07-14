package com.farmape.ms.ventas.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.farmape.ms.ventas.api.dto.ActualizarVentaRequest;
import com.farmape.ms.ventas.api.dto.CrearVentaRequest;
import com.farmape.ms.ventas.api.dto.VentaResponse;
import com.farmape.ms.ventas.application.service.VentaService;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VentaResponse registrarVenta(@RequestBody CrearVentaRequest request) {
        return ventaService.registrarVenta(request);
    }

    @GetMapping
    public List<VentaResponse> listarVentas() {
        return ventaService.listarVentas();
    }

    @GetMapping("/ultimas")
    public List<VentaResponse> listarUltimasVentas() {
        return ventaService.listarUltimasVentas();
    }

    @GetMapping("/{idVenta}")
    public VentaResponse obtenerVenta(@PathVariable Integer idVenta) {
        return ventaService.obtenerVenta(idVenta);
    }

    @GetMapping("/{idVenta}/detalle")
    public VentaResponse obtenerDetalleVenta(@PathVariable Integer idVenta) {
        return ventaService.obtenerDetalleVenta(idVenta);
    }

    @GetMapping("/cliente/{idCliente}")
    public List<VentaResponse> listarVentasPorCliente(@PathVariable Integer idCliente) {
        return ventaService.listarVentasPorCliente(idCliente);
    }

    @GetMapping("/estado/{estado}")
    public List<VentaResponse> listarVentasPorEstado(@PathVariable String estado) {
        return ventaService.listarVentasPorEstado(estado);
    }

    @PutMapping("/{idVenta}")
    public VentaResponse actualizarVenta(
            @PathVariable Integer idVenta,
            @RequestBody ActualizarVentaRequest request
    ) {
        return ventaService.actualizarVenta(idVenta, request);
    }

    @PatchMapping("/{idVenta}/completar")
    public VentaResponse completarVenta(@PathVariable Integer idVenta) {
        return ventaService.completarVenta(idVenta);
    }

    @PatchMapping("/{idVenta}/confirmar")
    public VentaResponse confirmarVenta(@PathVariable Integer idVenta) {
        return ventaService.completarVenta(idVenta);
    }

    @PatchMapping("/{idVenta}/cancelar")
    public VentaResponse cancelarVenta(@PathVariable Integer idVenta) {
        return ventaService.cancelarVenta(idVenta);
    }

    @PatchMapping("/{idVenta}/rechazar")
    public VentaResponse rechazarVenta(@PathVariable Integer idVenta) {
        return ventaService.rechazarVenta(idVenta);
    }
}
