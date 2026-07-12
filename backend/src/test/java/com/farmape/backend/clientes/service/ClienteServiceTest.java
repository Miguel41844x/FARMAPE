package com.farmape.backend.clientes.service;

import com.farmape.backend.clientes.dto.ClienteRequest;
import com.farmape.backend.clientes.enums.TipoCliente;
import com.farmape.backend.clientes.model.Cliente;
import com.farmape.backend.clientes.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {
    @Mock ClienteRepository clienteRepository;
    @InjectMocks ClienteService clienteService;

    private ClienteRequest request() {
        return new ClienteRequest("70000001", "Ana", "Rojas", "999111222",
                null, "Lima", "ana@correo.pe", TipoCliente.Natural);
    }

    @Test
    void crearGuardaClienteCuandoDocumentoEsUnico() {
        when(clienteRepository.existsByDniRuc("70000001")).thenReturn(false);
        when(clienteRepository.save(any())).thenAnswer(invocation -> {
            Cliente cliente = invocation.getArgument(0);
            cliente.setIdCliente(4);
            return cliente;
        });

        var response = clienteService.crear(request());

        assertThat(response.idCliente()).isEqualTo(4);
        assertThat(response.dniRuc()).isEqualTo("70000001");
    }

    @Test
    void crearRechazaDocumentoDuplicado() {
        when(clienteRepository.existsByDniRuc("70000001")).thenReturn(true);

        assertThatThrownBy(() -> clienteService.crear(request()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe");
        verify(clienteRepository, never()).save(any());
    }
}
