package com.farmape.backend.security;

import com.farmape.backend.model.CuentaUsuario;
import com.farmape.backend.model.enums.EstadoCuentaUsuario;
import com.farmape.backend.repository.CuentaUsuarioRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CuentaUsuarioRepository cuentaUsuarioRepository;

    public CustomUserDetailsService(CuentaUsuarioRepository cuentaUsuarioRepository) {
        this.cuentaUsuarioRepository = cuentaUsuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        CuentaUsuario cuenta = cuentaUsuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email no encontrado"));

        if (cuenta.getEstado() != EstadoCuentaUsuario.Activo) {
            throw new DisabledException("La cuenta no está activa");
        }

        return User.builder()
                .username(cuenta.getEmail())
                .password(cuenta.getClave())
                .roles(cuenta.getTrabajador().getRol().getNombreRol())
                .build();
    }
}