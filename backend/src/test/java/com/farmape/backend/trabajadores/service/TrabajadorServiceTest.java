package com.farmape.backend.trabajadores.service;

import com.farmape.backend.roles.model.Rol;
import com.farmape.backend.roles.repository.RolRepository;
import com.farmape.backend.trabajadores.dto.TrabajadorRequest;
import com.farmape.backend.trabajadores.enums.EstadoTrabajador;
import com.farmape.backend.trabajadores.model.Trabajador;
import com.farmape.backend.trabajadores.repository.TrabajadorRepository;
import com.farmape.backend.usuarios.repository.CuentaUsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrabajadorServiceTest {
    @Mock TrabajadorRepository trabajadorRepository;
    @Mock RolRepository rolRepository;
    @Mock CuentaUsuarioRepository cuentaUsuarioRepository;
    @InjectMocks TrabajadorService trabajadorService;

    @Test
    void crearAsignaEstadoActivoPorDefecto() {
        Rol rol = Rol.builder().idRol(2).codigo("CASHIER").nombreRol("Cajero").activo(true).build();
        TrabajadorRequest request = new TrabajadorRequest("70000003", "Mario", "Sol", null, null, 2, null);
        when(rolRepository.findById(2)).thenReturn(Optional.of(rol));
        when(trabajadorRepository.save(any())).thenAnswer(invocation -> {
            Trabajador trabajador = invocation.getArgument(0);
            trabajador.setIdTrabajador(9);
            return trabajador;
        });

        var response = trabajadorService.crear(request);

        assertThat(response.idTrabajador()).isEqualTo(9);
        assertThat(response.estado()).isEqualTo(EstadoTrabajador.Activo);
    }

    @Test
    void crearRechazaRolInexistente() {
        when(rolRepository.findById(99)).thenReturn(Optional.empty());
        TrabajadorRequest request = new TrabajadorRequest("1", "A", "B", null, null, 99, null);

        assertThatThrownBy(() -> trabajadorService.crear(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Rol no encontrado");
    }
}
