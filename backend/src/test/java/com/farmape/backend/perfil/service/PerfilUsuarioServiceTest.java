package com.farmape.backend.perfil.service;

import com.farmape.backend.perfil.dto.ActualizarPerfilRequest;
import com.farmape.backend.roles.model.Rol;
import com.farmape.backend.security.AuthenticatedUserService;
import com.farmape.backend.trabajadores.model.Trabajador;
import com.farmape.backend.trabajadores.repository.TrabajadorRepository;
import com.farmape.backend.usuarios.model.CuentaUsuario;
import com.farmape.backend.usuarios.repository.CuentaUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerfilUsuarioServiceTest {

    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private CuentaUsuarioRepository cuentaUsuarioRepository;
    @Mock
    private TrabajadorRepository trabajadorRepository;
    @InjectMocks
    private PerfilUsuarioService perfilUsuarioService;

    private CuentaUsuario cuenta;
    private Trabajador trabajador;

    @BeforeEach
    void configurarCuenta() {
        Rol rol = Rol.builder().idRol(2).nombreRol("Cajero").build();
        trabajador = Trabajador.builder()
                .idTrabajador(8)
                .dni("70400400")
                .nombres("Ana")
                .apellidos("Rojas")
                .rol(rol)
                .build();
        cuenta = CuentaUsuario.builder()
                .idCuenta(5)
                .usuario("arojas")
                .email("ana@farmape.pe")
                .trabajador(trabajador)
                .build();
    }

    @Test
    void obtenerPerfilActualMapeaCuentaAutenticada() {
        when(authenticatedUserService.currentAccount()).thenReturn(cuenta);

        var response = perfilUsuarioService.obtenerPerfilActual();

        assertThat(response.idCuenta()).isEqualTo(5);
        assertThat(response.usuario()).isEqualTo("arojas");
        assertThat(response.nombres()).isEqualTo("Ana");
        assertThat(response.rol()).isEqualTo("Cajero");
    }

    @Test
    void actualizarPerfilLimpiaCamposYGuardaAmbasEntidades() {
        when(authenticatedUserService.currentAccount()).thenReturn(cuenta);
        when(cuentaUsuarioRepository.findByEmail("nueva@farmape.pe")).thenReturn(Optional.empty());

        var response = perfilUsuarioService.actualizarPerfilActual(
                new ActualizarPerfilRequest(
                        "nueva@farmape.pe", "  Ana María  ", "  Rojas Díaz  ", "   ", "  Av. Perú 123  "
                )
        );

        assertThat(response.email()).isEqualTo("nueva@farmape.pe");
        assertThat(trabajador.getNombres()).isEqualTo("Ana María");
        assertThat(trabajador.getTelefono()).isNull();
        assertThat(trabajador.getDireccion()).isEqualTo("Av. Perú 123");
        verify(trabajadorRepository).save(trabajador);
        verify(cuentaUsuarioRepository).save(cuenta);
    }

    @Test
    void actualizarPerfilRechazaEmailDeOtraCuenta() {
        CuentaUsuario otraCuenta = CuentaUsuario.builder().idCuenta(99).build();
        when(authenticatedUserService.currentAccount()).thenReturn(cuenta);
        when(cuentaUsuarioRepository.findByEmail("usado@farmape.pe")).thenReturn(Optional.of(otraCuenta));

        assertThatThrownBy(() -> perfilUsuarioService.actualizarPerfilActual(
                new ActualizarPerfilRequest("usado@farmape.pe", "Ana", "Rojas", null, null)
        ))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("otra cuenta");

        verify(trabajadorRepository, never()).save(trabajador);
        verify(cuentaUsuarioRepository, never()).save(cuenta);
    }
}
