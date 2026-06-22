package com.farmape.backend.auditoria.controller;

import com.farmape.backend.auditoria.dto.AuditoriaEventoResponse;
import com.farmape.backend.auditoria.dto.AuditoriaResumenResponse;
import com.farmape.backend.auditoria.dto.RegistrarAuditoriaRequest;
import com.farmape.backend.auditoria.service.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping("/resumen")
    public AuditoriaResumenResponse obtenerResumen() {
        return auditoriaService.obtenerResumen();
    }

    @GetMapping("/eventos")
    public List<AuditoriaEventoResponse> listarEventos(
            @RequestParam(required = false) String modulo,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) String severidad,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false, name = "q") String busqueda,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Integer limite
    ) {
        return auditoriaService.listarEventos(modulo, accion, severidad, usuario, busqueda, desde, hasta, limite);
    }

    @PostMapping("/eventos")
    public AuditoriaEventoResponse registrarEvento(@Valid @RequestBody RegistrarAuditoriaRequest request,
                                                   HttpServletRequest servletRequest) {
        return auditoriaService.registrarManual(request, obtenerIp(servletRequest));
    }

    private String obtenerIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
