package com.farmape.backend.caja.controller;

import com.farmape.backend.caja.dto.RegistrarPagoRequest;
import com.farmape.backend.caja.dto.RegistrarPagoResponse;
import com.farmape.backend.caja.service.CajaService;
import com.farmape.backend.ventas.dto.OrdenVentaResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/caja")
public class CajaController {

    private final CajaService cajaService;

    public CajaController(CajaService cajaService) {
        this.cajaService = cajaService;
    }

    @GetMapping("/ordenes-pendientes")
    public List<OrdenVentaResponse> listarOrdenesPendientes() {
        return cajaService.listarOrdenesPendientes();
    }

    @GetMapping("/ordenes/{idOrdenVenta}")
    public OrdenVentaResponse obtenerOrden(@PathVariable Integer idOrdenVenta) {
        return cajaService.obtenerOrden(idOrdenVenta);
    }

    @PostMapping("/ordenes/{idOrdenVenta}/pagar")
    public RegistrarPagoResponse registrarPago(
            @PathVariable Integer idOrdenVenta,
            @Valid @RequestBody RegistrarPagoRequest request
    ) {
        return cajaService.registrarPago(idOrdenVenta, request);
    }
}