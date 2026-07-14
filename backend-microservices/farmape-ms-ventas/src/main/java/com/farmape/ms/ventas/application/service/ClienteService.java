package com.farmape.ms.ventas.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.farmape.ms.ventas.api.dto.ClienteRequest;
import com.farmape.ms.ventas.api.dto.ClienteResponse;
import com.farmape.ms.ventas.application.exception.VentaBusinessException;
import com.farmape.ms.ventas.application.exception.VentaNotFoundException;
import com.farmape.ms.ventas.domain.model.Cliente;
import com.farmape.ms.ventas.domain.repository.ClienteRepository;

@Service
public class ClienteService {

    private static final String TIPO_CLIENTE_DEFAULT = "Natural";

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<ClienteResponse> listarClientes() {
        return clienteRepository.findAllByOrderByFechaRegistroDesc()
                .stream()
                .map(this::toClienteResponse)
                .toList();
    }

    public ClienteResponse buscarPorDocumento(String documento) {
        String dniRuc = documentoSeguro(documento);
        return clienteRepository.findByDniRuc(dniRuc)
                .map(this::toClienteResponse)
                .orElseThrow(() -> new VentaNotFoundException("Cliente no encontrado: " + dniRuc));
    }

    public ClienteResponse registrarCliente(ClienteRequest request) {
        if (request == null) {
            throw new VentaBusinessException("El cliente es obligatorio.");
        }

        String dniRuc = documentoSeguro(documentoRequest(request));
        if (clienteRepository.existsByDniRuc(dniRuc)) {
            throw new VentaBusinessException("Ya existe un cliente con ese DNI/RUC.");
        }

        String nombres = texto(request.nombres());
        if (nombres == null) {
            throw new VentaBusinessException("Los nombres del cliente son obligatorios.");
        }

        Cliente cliente = new Cliente();
        cliente.setIdCliente(siguienteIdCliente());
        cliente.setTipoCliente(valorODefault(request.tipoCliente(), TIPO_CLIENTE_DEFAULT));
        cliente.setDniRuc(dniRuc);
        cliente.setNombres(nombres);
        cliente.setApellidos(texto(request.apellidos()));
        cliente.setTelefono(texto(request.telefono()));
        cliente.setWhatsapp(texto(request.whatsapp()));
        cliente.setDireccion(texto(request.direccion()));
        cliente.setEmail(texto(request.email()));
        cliente.setFechaRegistro(LocalDateTime.now());

        return toClienteResponse(clienteRepository.save(cliente));
    }

    private String documentoRequest(ClienteRequest request) {
        return request.dniRuc() != null && !request.dniRuc().isBlank()
                ? request.dniRuc()
                : request.documento();
    }

    private String documentoSeguro(String documento) {
        String dniRuc = texto(documento);
        if (dniRuc == null) {
            throw new VentaBusinessException("El DNI/RUC del cliente es obligatorio.");
        }
        return dniRuc;
    }

    private String valorODefault(String valor, String defaultValue) {
        String texto = texto(valor);
        return texto == null ? defaultValue : texto;
    }

    private String texto(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    private synchronized Integer siguienteIdCliente() {
        return clienteRepository.findTopByOrderByIdClienteDesc()
                .map(Cliente::getIdCliente)
                .map(id -> id + 1)
                .orElse(1);
    }

    private ClienteResponse toClienteResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getIdCliente(),
                cliente.getTipoCliente(),
                cliente.getDniRuc(),
                cliente.getDniRuc(),
                cliente.getNombres(),
                cliente.getApellidos(),
                cliente.getTelefono(),
                cliente.getWhatsapp(),
                cliente.getDireccion(),
                cliente.getEmail(),
                cliente.getFechaRegistro()
        );
    }
}
