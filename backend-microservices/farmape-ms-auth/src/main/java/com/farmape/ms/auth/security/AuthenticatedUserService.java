package com.farmape.ms.auth.security;

import com.farmape.ms.auth.usuarios.model.CuentaUsuario;
import com.farmape.ms.auth.usuarios.repository.CuentaUsuarioRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService {

    private final CuentaUsuarioRepository cuentaUsuarioRepository;

    public AuthenticatedUserService(CuentaUsuarioRepository cuentaUsuarioRepository) {
        this.cuentaUsuarioRepository = cuentaUsuarioRepository;
    }

    public CuentaUsuario currentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("No hay una cuenta autenticada");
        }

        String usuario = authentication.getName();

        return cuentaUsuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new IllegalStateException("La cuenta autenticada no existe"));
    }
}