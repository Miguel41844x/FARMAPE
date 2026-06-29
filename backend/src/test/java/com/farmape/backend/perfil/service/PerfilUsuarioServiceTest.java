package com.farmape.backend.perfil.service;

import com.farmape.backend.perfil.dto.ActualizarPerfilRequest;
import com.farmape.backend.security.AuthenticatedUserService;
import com.farmape.backend.support.TestDataFactory;
import com.farmape.backend.trabajadores.repository.TrabajadorRepository;
import com.farmape.backend.usuarios.model.CuentaUsuario;
import com.farmape.backend.usuarios.repository.CuentaUsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerfilUsuarioServiceTest {
    @Mock AuthenticatedUserService authenticatedUserService;
    @Mock CuentaUsuarioRepository cuentaUsuarioRepository;
    @Mock TrabajadorRepository trabajadorRepository;
    @InjectMocks PerfilUsuarioService perfilUsuarioService;

    @Test
    void obtenerPerfilActualMapeaLaCuentaAutenticada() {
        CuentaUsuario cuenta = TestDataFactory.cuentaAdministradorActiva();
        when(authenticatedUserService.currentAccount()).thenReturn(cuenta);

        var response = perfilUsuarioService.obtenerPerfilActual();

        assertThat(response.usuario()).isEqualTo("admin");
        assertThat(response.rol()).isEqualTo("Administrador");
    }

    @Test
    void actualizarPerfilRechazaEmailDeOtraCuenta() {
        CuentaUsuario cuenta = TestDataFactory.cuentaAdministradorActiva();
        CuentaUsuario otra = CuentaUsuario.builder().idCuenta(99).build();
        when(authenticatedUserService.currentAccount()).thenReturn(cuenta);
        when(cuentaUsuarioRepository.findByEmail("ocupado@correo.pe")).thenReturn(Optional.of(otra));

        assertThatThrownBy(() -> perfilUsuarioService.actualizarPerfilActual(
                new ActualizarPerfilRequest("ocupado@correo.pe", "Ana", "Torres", null, null)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("otra cuenta");
    }
}
