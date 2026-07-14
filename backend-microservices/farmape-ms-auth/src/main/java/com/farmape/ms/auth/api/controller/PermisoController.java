package com.farmape.ms.auth.roles.controller;

import com.farmape.ms.auth.roles.dto.PermisoResponse;
import com.farmape.ms.auth.roles.service.RolService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/permisos")
public class PermisoController {

    private final RolService rolService;

    public PermisoController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public List<PermisoResponse> listar() {
        return rolService.listarPermisos();
    }
}
