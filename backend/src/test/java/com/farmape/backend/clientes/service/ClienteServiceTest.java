package com.farmape.backend.clientes.service;

import com.farmape.backend.clientes.dto.ClienteRequest;
import com.farmape.backend.clientes.dto.ClienteResponse;
import com.farmape.backend.clientes.enums.TipoCliente;
import com.farmape.backend.clientes.model.Cliente;
import com.farmape.backend.clientes.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void crearGuardaClienteCuandoDocumentoNoExiste() {
        ClienteRequest request = new ClienteRequest(
                "12345678",
                "Ana",
                "Lopez",
                "999111222",
                "999111222",
                "Av. Peru 123",
                "ana@example.com",
                TipoCliente.Natural
        );

        when(clienteRepository.existsByDniRuc("12345678")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente cliente = invocation.getArgument(0);
            cliente.setIdCliente(7);
            return cliente;
        });

        ClienteResponse response = clienteService.crear(request);

        assertThat(response.idCliente()).isEqualTo(7);
        assertThat(response.dniRuc()).isEqualTo("12345678");
        assertThat(response.nombres()).isEqualTo("Ana");
        assertThat(response.tipoCliente()).isEqualTo(TipoCliente.Natural);

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("ana@example.com");
    }

    @Test
    void crearLanzaErrorCuandoDocumentoYaExiste() {
        ClienteRequest request = new ClienteRequest(
                "12345678",
                "Ana",
                "Lopez",
                null,
                null,
                null,
                null,
                TipoCliente.Natural
        );

        when(clienteRepository.existsByDniRuc("12345678")).thenReturn(true);

        assertThatThrownBy(() -> clienteService.crear(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe un cliente con ese DNI/RUC");

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void obtenerPorDocumentoDevuelveClienteEncontrado() {
        Cliente cliente = Cliente.builder()
                .idCliente(5)
                .dniRuc("20600011122")
                .nombres("Botica Central")
                .tipoCliente(TipoCliente.Empresa)
                .build();

        when(clienteRepository.findByDniRuc("20600011122")).thenReturn(Optional.of(cliente));

        ClienteResponse response = clienteService.obtenerPorDocumento("20600011122");

        assertThat(response.idCliente()).isEqualTo(5);
        assertThat(response.dniRuc()).isEqualTo("20600011122");
        assertThat(response.nombres()).isEqualTo("Botica Central");
        assertThat(response.tipoCliente()).isEqualTo(TipoCliente.Empresa);
    }
}
