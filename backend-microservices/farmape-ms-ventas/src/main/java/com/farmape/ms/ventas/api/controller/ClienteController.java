package com.farmape.ms.ventas.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.farmape.ms.ventas.api.dto.ClienteRequest;
import com.farmape.ms.ventas.api.dto.ClienteResponse;
import com.farmape.ms.ventas.application.service.ClienteService;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<ClienteResponse> listarClientes() {
        return clienteService.listarClientes();
    }

    @GetMapping("/documento/{documento}")
    public ClienteResponse buscarPorDocumento(@PathVariable String documento) {
        return clienteService.buscarPorDocumento(documento);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse registrarCliente(@RequestBody ClienteRequest request) {
        return clienteService.registrarCliente(request);
    }
}
