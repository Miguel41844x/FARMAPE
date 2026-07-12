package com.farmape.backend.security;

import com.farmape.backend.support.TestDataFactory;
import com.farmape.backend.trabajadores.enums.EstadoTrabajador;
import com.farmape.backend.usuarios.enums.EstadoCuentaUsuario;
import com.farmape.backend.usuarios.model.CuentaUsuario;
import com.farmape.backend.usuarios.repository.CuentaUsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private CuentaUsuarioRepository cuentaUsuarioRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadUserByUsernameCargaCuentaActivaConPermisosActivos() {
        CuentaUsuario cuenta = TestDataFactory.cuentaAdministradorActiva();
        when(cuentaUsuarioRepository.findByUsuarioOrEmail("admin", "admin"))
                .thenReturn(Optional.of(cuenta));

        var userDetails = userDetailsService.loadUserByUsername("admin");

        assertThat(userDetails.getUsername()).isEqualTo("admin");
        assertThat(userDetails.getPassword()).isEqualTo("clave-codificada");
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("VENTAS_VER");
    }

    @Test
    void loadUserByUsernameLanzaErrorCuandoNoExiste() {
        when(cuentaUsuarioRepository.findByUsuarioOrEmail("desconocido", "desconocido"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("desconocido"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuario o email no encontrado");
    }

    @Test
    void loadUserByUsernameRechazaCuentaInactiva() {
        CuentaUsuario cuenta = TestDataFactory.cuentaAdministradorActiva();
        cuenta.setEstado(EstadoCuentaUsuario.Bloqueado);
        when(cuentaUsuarioRepository.findByUsuarioOrEmail("admin", "admin"))
                .thenReturn(Optional.of(cuenta));

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("admin"))
                .isInstanceOf(DisabledException.class)
                .hasMessage("La cuenta no está activa");
    }

    @Test
    void loadUserByUsernameRechazaTrabajadorInactivo() {
        CuentaUsuario cuenta = TestDataFactory.cuentaAdministradorActiva();
        cuenta.getTrabajador().setEstado(EstadoTrabajador.Inactivo);
        when(cuentaUsuarioRepository.findByUsuarioOrEmail("admin", "admin"))
                .thenReturn(Optional.of(cuenta));

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("admin"))
                .isInstanceOf(DisabledException.class)
                .hasMessage("El trabajador no está activo");
    }
}
