package com.farmape.backend.almacen.controller;

import com.farmape.backend.almacen.dto.*;
import com.farmape.backend.almacen.service.AlmacenService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/almacen")
public class AlmacenController {

    private final AlmacenService almacenService;

    public AlmacenController(AlmacenService almacenService) {
        this.almacenService = almacenService;
    }

    @GetMapping("/ingresos")
    public List<IngresoAlmacenResponse> listarIngresos() {
        return almacenService.listarIngresos();
    }

    @PostMapping("/ingresos")
    public IngresoAlmacenResponse registrarIngreso(@Valid @RequestBody RegistrarIngresoAlmacenRequest request) {
        return almacenService.registrarIngreso(request);
    }

    @GetMapping("/verificaciones")
    public List<VerificacionProductoResponse> listarVerificaciones() {
        return almacenService.listarVerificaciones();
    }

    @PatchMapping("/verificaciones/{idVerificacion}/confirmar")
    public VerificacionProductoResponse confirmarVerificacion(@PathVariable Integer idVerificacion) {
        return almacenService.confirmarVerificacion(idVerificacion);
    }

    @PatchMapping("/verificaciones/{idVerificacion}/observar")
    public VerificacionProductoResponse observarVerificacion(@PathVariable Integer idVerificacion) {
        return almacenService.observarVerificacion(idVerificacion);
    }

    @GetMapping("/informe")
    public List<InformeAlmacenResponse> obtenerInforme(@RequestParam(defaultValue = "HOY") String periodo) {
        return almacenService.obtenerInforme(periodo);
    }
}
