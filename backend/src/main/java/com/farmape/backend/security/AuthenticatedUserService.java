package com.farmape.backend.security;

import com.farmape.backend.usuarios.model.CuentaUsuario;
import com.farmape.backend.usuarios.repository.CuentaUsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService {

    private final CuentaUsuarioRepository cuentaUsuarioRepository;

    public AuthenticatedUserService(CuentaUsuarioRepository cuentaUsuarioRepository) {
        this.cuentaUsuarioRepository = cuentaUsuarioRepository;
    }

    public CuentaUsuario currentAccount() {
        String usuario = SecurityContextHolder.getContext().getAuthentication().getName();
        return cuentaUsuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new IllegalStateException("La cuenta autenticada no existe"));
    }
}
