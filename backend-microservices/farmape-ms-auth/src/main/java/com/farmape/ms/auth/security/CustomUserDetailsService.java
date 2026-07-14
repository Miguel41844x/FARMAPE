package com.farmape.ms.auth.security;

import com.farmape.ms.auth.usuarios.enums.EstadoCuentaUsuario;
import com.farmape.ms.auth.trabajadores.enums.EstadoTrabajador;
import com.farmape.ms.auth.usuarios.model.CuentaUsuario;
import com.farmape.ms.auth.usuarios.repository.CuentaUsuarioRepository;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CuentaUsuarioRepository cuentaUsuarioRepository;

    public CustomUserDetailsService(CuentaUsuarioRepository cuentaUsuarioRepository) {
        this.cuentaUsuarioRepository = cuentaUsuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CuentaUsuario cuenta = cuentaUsuarioRepository
                .findByUsuarioOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario o email no encontrado"));

        if (cuenta.getEstado() != EstadoCuentaUsuario.Activo)
            throw new DisabledException("La cuenta no está activa");

        if (cuenta.getTrabajador().getEstado() != EstadoTrabajador.Activo)
            throw new DisabledException("El trabajador no está activo");

        return User.builder()
                .username(cuenta.getUsuario())
                .password(cuenta.getClave())
                .authorities(cuenta.getTrabajador().getRol().getPermisos().stream()
                        .filter(permiso -> Boolean.TRUE.equals(permiso.getActivo()))
                        .map(permiso -> new SimpleGrantedAuthority(permiso.getCodigo()))
                        .toList())
                .build();
    }
}