package com.farmape.backend.reportes.controller;

import com.farmape.backend.reportes.dto.AccionGerenciaResponse;
import com.farmape.backend.reportes.dto.ActualizarEstadoAccionRequest;
import com.farmape.backend.reportes.dto.InformeResponse;
import com.farmape.backend.reportes.dto.RegistrarAccionRequest;
import com.farmape.backend.reportes.dto.RegistrarInformeRequest;
import com.farmape.backend.reportes.dto.ReporteResumenResponse;
import com.farmape.backend.reportes.service.ReportesService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReportesController {

    private final ReportesService reportesService;

    public ReportesController(ReportesService reportesService) {
        this.reportesService = reportesService;
    }

    @GetMapping("/resumen")
    public ReporteResumenResponse obtenerResumen() {
        return reportesService.obtenerResumen();
    }

    @GetMapping("/informes")
    public List<InformeResponse> listarInformes() {
        return reportesService.listarInformes();
    }

    @PostMapping("/informes")
    public InformeResponse registrarInforme(@Valid @RequestBody RegistrarInformeRequest request) {
        return reportesService.registrarInforme(request);
    }

    @GetMapping("/acciones")
    public List<AccionGerenciaResponse> listarAcciones() {
        return reportesService.listarAcciones();
    }

    @PostMapping("/acciones")
    public AccionGerenciaResponse registrarAccion(@Valid @RequestBody RegistrarAccionRequest request) {
        return reportesService.registrarAccion(request);
    }

    @PatchMapping("/acciones/{idAccion}/estado")
    public AccionGerenciaResponse actualizarEstadoAccion(
            @PathVariable Integer idAccion,
            @Valid @RequestBody ActualizarEstadoAccionRequest request
    ) {
        return reportesService.actualizarEstadoAccion(idAccion, request);
    }
}
