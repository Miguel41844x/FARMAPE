package com.farmape.backend.despacho.controller;

import com.farmape.backend.despacho.dto.OrdenTiendaResponse;
import com.farmape.backend.despacho.dto.RepartoDomicilioResponse;
import com.farmape.backend.despacho.service.DespachoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/despacho")
public class DespachoController {

    private final DespachoService despachoService;

    public DespachoController(DespachoService despachoService) {
        this.despachoService = despachoService;
    }

    @GetMapping("/ordenes-tienda")
    public List<OrdenTiendaResponse> listarOrdenesTienda() {
        return despachoService.listarOrdenesTienda();
    }

    @PatchMapping("/ordenes-tienda/{idOrdenVenta}/entregar")
    public OrdenTiendaResponse entregarOrdenTienda(@PathVariable Integer idOrdenVenta) {
        return despachoService.entregarOrdenTienda(idOrdenVenta);
    }

    @GetMapping("/repartos")
    public List<RepartoDomicilioResponse> listarRepartos() {
        return despachoService.listarRepartosDomicilio();
    }

    @PostMapping("/repartos/orden/{idOrdenVenta}")
    public RepartoDomicilioResponse crearReparto(@PathVariable Integer idOrdenVenta) {
        return despachoService.crearRepartoDesdeOrden(idOrdenVenta);
    }

    @PatchMapping("/repartos/{idReparto}/entregar")
    public RepartoDomicilioResponse entregarReparto(@PathVariable Integer idReparto) {
        return despachoService.entregarReparto(idReparto);
    }
}
