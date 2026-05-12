package com.farmape.backend.security;

import com.farmape.backend.usuarios.enums.EstadoCuentaUsuario;
import com.farmape.backend.usuarios.model.CuentaUsuario;
import com.farmape.backend.usuarios.repository.CuentaUsuarioRepository;

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
    public UserDetails loadUserByUsername(String usuario) throws UsernameNotFoundException {

        CuentaUsuario cuenta = cuentaUsuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (cuenta.getEstado() != EstadoCuentaUsuario.Activo) {
            throw new DisabledException("La cuenta no está activa");
        }

        return User.builder()
                .username(cuenta.getUsuario())
                .password(cuenta.getClave())
                .roles(cuenta.getTrabajador().getRol().getNombreRol())
                .build();
    }
}