package com.farmape.backend.roles.controller;

import org.springframework.web.bind.annotation.*;

import com.farmape.backend.roles.model.Rol;
import com.farmape.backend.roles.service.RolService;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public List<Rol> listarRoles() {
        return rolService.listarRoles();
    }
}