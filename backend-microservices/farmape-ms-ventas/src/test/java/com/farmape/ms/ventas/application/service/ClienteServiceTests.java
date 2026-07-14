package com.farmape.ms.ventas.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.farmape.ms.ventas.api.dto.ClienteRequest;
import com.farmape.ms.ventas.application.exception.VentaBusinessException;
import com.farmape.ms.ventas.application.exception.VentaNotFoundException;
import com.farmape.ms.ventas.domain.model.Cliente;
import com.farmape.ms.ventas.domain.repository.ClienteRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTests {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void listarClientesDevuelveRespuestaCompatibleConFrontend() {
        when(clienteRepository.findAllByOrderByFechaRegistroDesc())
                .thenReturn(List.of(cliente(2, "76543210", "Ana", "Perez")));

        var response = clienteService.listarClientes();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).idCliente()).isEqualTo(2);
        assertThat(response.get(0).dniRuc()).isEqualTo("76543210");
        assertThat(response.get(0).documento()).isEqualTo("76543210");
    }

    @Test
    void buscarPorDocumentoDevuelveCliente() {
        when(clienteRepository.findByDniRuc("76543210"))
                .thenReturn(Optional.of(cliente(2, "76543210", "Ana", "Perez")));

        var response = clienteService.buscarPorDocumento(" 76543210 ");

        assertThat(response.idCliente()).isEqualTo(2);
        assertThat(response.nombres()).isEqualTo("Ana");
    }

    @Test
    void buscarPorDocumentoFallaCuandoNoExiste() {
        when(clienteRepository.findByDniRuc("99999999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.buscarPorDocumento("99999999"))
                .isInstanceOf(VentaNotFoundException.class)
                .hasMessageContaining("Cliente no encontrado");
    }

    @Test
    void registrarClienteAsignaIdYNormalizaDatos() {
        when(clienteRepository.existsByDniRuc("76543210")).thenReturn(false);
        when(clienteRepository.findTopByOrderByIdClienteDesc())
                .thenReturn(Optional.of(cliente(5, "70987654", "Rosa", "Salazar")));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = clienteService.registrarCliente(new ClienteRequest(
                "Natural",
                " 76543210 ",
                null,
                " Ana ",
                " Perez ",
                "987654321",
                "987654321",
                "Av. Los Jardines 123",
                "ana.perez@example.com"
        ));

        assertThat(response.idCliente()).isEqualTo(6);
        assertThat(response.dniRuc()).isEqualTo("76543210");
        assertThat(response.nombres()).isEqualTo("Ana");
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void registrarClienteFallaConDocumentoDuplicado() {
        when(clienteRepository.existsByDniRuc("76543210")).thenReturn(true);

        assertThatThrownBy(() -> clienteService.registrarCliente(new ClienteRequest(
                "Natural",
                "76543210",
                null,
                "Ana",
                "Perez",
                null,
                null,
                null,
                null
        )))
                .isInstanceOf(VentaBusinessException.class)
                .hasMessageContaining("Ya existe");
    }

    private Cliente cliente(Integer idCliente, String dniRuc, String nombres, String apellidos) {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(idCliente);
        cliente.setTipoCliente("Natural");
        cliente.setDniRuc(dniRuc);
        cliente.setNombres(nombres);
        cliente.setApellidos(apellidos);
        cliente.setTelefono("987654321");
        cliente.setWhatsapp("987654321");
        cliente.setDireccion("Direccion " + idCliente);
        cliente.setEmail("cliente" + idCliente + "@example.com");
        cliente.setFechaRegistro(LocalDateTime.now());
        return cliente;
    }
}
