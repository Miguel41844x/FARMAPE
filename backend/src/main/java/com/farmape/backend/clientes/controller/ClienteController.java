package com.farmape.backend.clientes.controller;

import com.farmape.backend.clientes.dto.ClienteRequest;
import com.farmape.backend.clientes.dto.ClienteResponse;
import com.farmape.backend.clientes.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<ClienteResponse> listar() {
        return clienteService.listar();
    }

    @GetMapping("/documento/{dniRuc}")
    public ClienteResponse obtenerPorDocumento(@PathVariable String dniRuc) {
        return clienteService.obtenerPorDocumento(dniRuc);
    }

    @GetMapping("/{id}")
    public ClienteResponse obtenerPorId(@PathVariable Integer id) {
        return clienteService.obtenerPorId(id);
    }

    @PostMapping
    public ClienteResponse crear(@Valid @RequestBody ClienteRequest request) {
        return clienteService.crear(request);
    }

    @PutMapping("/{id}")
    public ClienteResponse actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ClienteRequest request
    ) {
        return clienteService.actualizar(id, request);
    }
}