package com.farmape.backend.security;

import com.farmape.backend.support.TestDataFactory;
import com.farmape.backend.usuarios.model.CuentaUsuario;
import com.farmape.backend.usuarios.repository.CuentaUsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserServiceTest {

    @Mock
    private CuentaUsuarioRepository cuentaUsuarioRepository;

    @InjectMocks
    private AuthenticatedUserService authenticatedUserService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void currentAccountRetornaCuentaDelUsuarioAutenticado() {
        CuentaUsuario cuenta = TestDataFactory.cuentaAdministradorActiva();
        var authentication = new UsernamePasswordAuthenticationToken(
                "admin", null, List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(cuentaUsuarioRepository.findByUsuario("admin")).thenReturn(Optional.of(cuenta));

        CuentaUsuario resultado = authenticatedUserService.currentAccount();

        assertThat(resultado).isSameAs(cuenta);
        verify(cuentaUsuarioRepository).findByUsuario("admin");
    }

    @Test
    void currentAccountLanzaErrorCuandoNoHayAutenticacion() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(authenticatedUserService::currentAccount)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No hay una cuenta autenticada");

        verifyNoInteractions(cuentaUsuarioRepository);
    }

    @Test
    void currentAccountLanzaErrorCuandoLaCuentaYaNoExiste() {
        var authentication = new UsernamePasswordAuthenticationToken(
                "eliminado", null, List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(cuentaUsuarioRepository.findByUsuario("eliminado")).thenReturn(Optional.empty());

        assertThatThrownBy(authenticatedUserService::currentAccount)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("La cuenta autenticada no existe");
    }
}
