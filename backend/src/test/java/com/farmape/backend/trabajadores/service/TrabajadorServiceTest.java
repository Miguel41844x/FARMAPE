package com.farmape.backend.trabajadores.service;

import com.farmape.backend.roles.model.Rol;
import com.farmape.backend.roles.repository.RolRepository;
import com.farmape.backend.trabajadores.dto.TrabajadorRequest;
import com.farmape.backend.trabajadores.dto.TrabajadorResponse;
import com.farmape.backend.trabajadores.enums.EstadoTrabajador;
import com.farmape.backend.trabajadores.model.Trabajador;
import com.farmape.backend.trabajadores.repository.TrabajadorRepository;
import com.farmape.backend.usuarios.enums.EstadoCuentaUsuario;
import com.farmape.backend.usuarios.model.CuentaUsuario;
import com.farmape.backend.usuarios.repository.CuentaUsuarioRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrabajadorServiceTest {

    @Mock
    private TrabajadorRepository trabajadorRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private CuentaUsuarioRepository cuentaUsuarioRepository;

    @InjectMocks
    private TrabajadorService trabajadorService;

    @Test
    void crearAsignaEstadoActivoCuandoNoSeEnviaEstado() {
        Rol rol = Rol.builder()
                .idRol(2)
                .nombreRol("Cajero")
                .codigo("CASHIER")
                .activo(true)
                .build();
        TrabajadorRequest request = new TrabajadorRequest(
                "70500111",
                "Luis",
                "Ramos",
                "955555555",
                "Av. Lima 456",
                2,
                null
        );

        when(rolRepository.findById(2)).thenReturn(Optional.of(rol));
        when(trabajadorRepository.save(any(Trabajador.class))).thenAnswer(invocation -> {
            Trabajador trabajador = invocation.getArgument(0);
            trabajador.setIdTrabajador(8);
            return trabajador;
        });

        TrabajadorResponse response = trabajadorService.crear(request);

        assertThat(response.idTrabajador()).isEqualTo(8);
        assertThat(response.dni()).isEqualTo("70500111");
        assertThat(response.rol()).isEqualTo("Cajero");
        assertThat(response.estado()).isEqualTo(EstadoTrabajador.Activo);

        ArgumentCaptor<Trabajador> captor = ArgumentCaptor.forClass(Trabajador.class);
        verify(trabajadorRepository).save(captor.capture());
        assertThat(captor.getValue().getFechaRegistro()).isNotNull();
    }

    @Test
    void cambiarEstadoInactivaCuentaUsuarioAsociada() {
        Rol rol = Rol.builder()
                .idRol(1)
                .nombreRol("Administrador")
                .codigo("ADMIN")
                .activo(true)
                .build();
        Trabajador trabajador = Trabajador.builder()
                .idTrabajador(10)
                .dni("12345678")
                .nombres("Maria")
                .apellidos("Torres")
                .rol(rol)
                .estado(EstadoTrabajador.Activo)
                .build();
        CuentaUsuario cuenta = CuentaUsuario.builder()
                .idCuenta(20)
                .trabajador(trabajador)
                .usuario("mtorres")
                .email("maria@example.com")
                .clave("secret")
                .estado(EstadoCuentaUsuario.Activo)
                .build();

        when(trabajadorRepository.findById(10)).thenReturn(Optional.of(trabajador));
        when(cuentaUsuarioRepository.findByTrabajador_IdTrabajador(10)).thenReturn(Optional.of(cuenta));
        when(trabajadorRepository.save(trabajador)).thenReturn(trabajador);

        TrabajadorResponse response = trabajadorService.cambiarEstado(10, EstadoTrabajador.Inactivo);

        assertThat(response.estado()).isEqualTo(EstadoTrabajador.Inactivo);
        assertThat(cuenta.getEstado()).isEqualTo(EstadoCuentaUsuario.Inactivo);
        verify(cuentaUsuarioRepository).save(cuenta);
        verify(trabajadorRepository).save(trabajador);
    }

    @Test
    void obtenerPorIdLanzaErrorCuandoTrabajadorNoExiste() {
        when(trabajadorRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trabajadorService.obtenerPorId(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Trabajador no encontrado");
    }
}
