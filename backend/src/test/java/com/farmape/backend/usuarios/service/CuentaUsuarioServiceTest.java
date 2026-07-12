package com.farmape.backend.usuarios.service;

import com.farmape.backend.roles.repository.RolRepository;
import com.farmape.backend.support.TestDataFactory;
import com.farmape.backend.trabajadores.repository.TrabajadorRepository;
import com.farmape.backend.usuarios.dto.CambiarClaveUsuarioRequest;
import com.farmape.backend.usuarios.dto.CrearUsuarioRequest;
import com.farmape.backend.usuarios.model.CuentaUsuario;
import com.farmape.backend.usuarios.repository.CuentaUsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuentaUsuarioServiceTest {
    @Mock CuentaUsuarioRepository cuentaUsuarioRepository;
    @Mock TrabajadorRepository trabajadorRepository;
    @Mock RolRepository rolRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks CuentaUsuarioService cuentaUsuarioService;

    @Test
    void crearRechazaNombreDeUsuarioDuplicado() {
        CrearUsuarioRequest request = new CrearUsuarioRequest("70000002", "Luis", "Paz", null,
                null, "lpaz", "luis@correo.pe", "secreto", 1, null);
        when(trabajadorRepository.existsByDni("70000002")).thenReturn(false);
        when(cuentaUsuarioRepository.existsByUsuario("lpaz")).thenReturn(true);

        assertThatThrownBy(() -> cuentaUsuarioService.crear(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("usuario");
        verify(cuentaUsuarioRepository, never()).save(any());
    }

    @Test
    void cambiarClaveAdministrativaCodificaLaNuevaClave() {
        CuentaUsuario cuenta = TestDataFactory.cuentaAdministradorActiva();
        when(cuentaUsuarioRepository.findById(10)).thenReturn(Optional.of(cuenta));
        when(passwordEncoder.encode("nueva-clave")).thenReturn("hash-nuevo");
        when(cuentaUsuarioRepository.save(cuenta)).thenReturn(cuenta);

        cuentaUsuarioService.cambiarClaveAdministrativa(10, new CambiarClaveUsuarioRequest("nueva-clave"));

        assertThat(cuenta.getClave()).isEqualTo("hash-nuevo");
        verify(passwordEncoder).encode("nueva-clave");
    }
}
