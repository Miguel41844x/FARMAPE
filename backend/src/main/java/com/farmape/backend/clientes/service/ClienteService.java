package com.farmape.backend.clientes.service;

import com.farmape.backend.clientes.dto.ClienteRequest;
import com.farmape.backend.clientes.dto.ClienteResponse;
import com.farmape.backend.clientes.model.Cliente;
import com.farmape.backend.clientes.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<ClienteResponse> listar() {
        return clienteRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ClienteResponse obtenerPorId(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        return toResponse(cliente);
    }

    public ClienteResponse crear(ClienteRequest request) {
        if (clienteRepository.existsByDniRuc(request.dniRuc())) {
            throw new RuntimeException("Ya existe un cliente con ese DNI/RUC");
        }

        Cliente cliente = Cliente.builder()
                .dniRuc(request.dniRuc())
                .nombres(request.nombres())
                .apellidos(request.apellidos())
                .telefono(request.telefono())
                .whatsapp(request.whatsapp())
                .direccion(request.direccion())
                .email(request.email())
                .tipoCliente(request.tipoCliente())
                .build();

        return toResponse(clienteRepository.save(cliente));
    }

    public ClienteResponse actualizar(Integer id, ClienteRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        cliente.setDniRuc(request.dniRuc());
        cliente.setNombres(request.nombres());
        cliente.setApellidos(request.apellidos());
        cliente.setTelefono(request.telefono());
        cliente.setWhatsapp(request.whatsapp());
        cliente.setDireccion(request.direccion());
        cliente.setEmail(request.email());
        cliente.setTipoCliente(request.tipoCliente());

        return toResponse(clienteRepository.save(cliente));
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getIdCliente(),
                cliente.getDniRuc(),
                cliente.getNombres(),
                cliente.getApellidos(),
                cliente.getTelefono(),
                cliente.getWhatsapp(),
                cliente.getDireccion(),
                cliente.getEmail(),
                cliente.getTipoCliente()
        );
    }
}