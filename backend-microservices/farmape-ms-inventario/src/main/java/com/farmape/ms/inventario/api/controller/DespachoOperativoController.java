package com.farmape.ms.inventario.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farmape.ms.inventario.api.dto.OrdenTiendaResponse;
import com.farmape.ms.inventario.api.dto.RepartoDomicilioResponse;
import com.farmape.ms.inventario.application.service.DespachoOperativoService;

@RestController
@RequestMapping({"/api/despacho", "/api/inventario/despacho"})
public class DespachoOperativoController {

    private final DespachoOperativoService despachoOperativoService;

    public DespachoOperativoController(DespachoOperativoService despachoOperativoService) {
        this.despachoOperativoService = despachoOperativoService;
    }

    @GetMapping("/ordenes-tienda")
    public List<OrdenTiendaResponse> listarOrdenesTienda() {
        return despachoOperativoService.listarOrdenesTienda();
    }

    @PatchMapping("/ordenes-tienda/{idOrdenVenta}/entregar")
    public OrdenTiendaResponse entregarOrdenTienda(@PathVariable Integer idOrdenVenta) {
        return despachoOperativoService.entregarOrdenTienda(idOrdenVenta);
    }

    @GetMapping("/repartos")
    public List<RepartoDomicilioResponse> listarRepartosDomicilio() {
        return despachoOperativoService.listarRepartosDomicilio();
    }

    @PatchMapping("/repartos/{idReparto}/entregar")
    public RepartoDomicilioResponse entregarReparto(@PathVariable Integer idReparto) {
        return despachoOperativoService.entregarReparto(idReparto);
    }
}
