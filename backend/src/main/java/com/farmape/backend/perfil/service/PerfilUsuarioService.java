package com.farmape.backend.perfil.service;

import com.farmape.backend.perfil.dto.ActualizarPerfilRequest;
import com.farmape.backend.perfil.dto.PerfilUsuarioResponse;
import com.farmape.backend.security.AuthenticatedUserService;
import com.farmape.backend.trabajadores.model.Trabajador;
import com.farmape.backend.trabajadores.repository.TrabajadorRepository;
import com.farmape.backend.usuarios.model.CuentaUsuario;
import com.farmape.backend.usuarios.repository.CuentaUsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PerfilUsuarioService {

    private final AuthenticatedUserService authenticatedUserService;
    private final CuentaUsuarioRepository cuentaUsuarioRepository;
    private final TrabajadorRepository trabajadorRepository;

    public PerfilUsuarioService(
            AuthenticatedUserService authenticatedUserService,
            CuentaUsuarioRepository cuentaUsuarioRepository,
            TrabajadorRepository trabajadorRepository
    ) {
        this.authenticatedUserService = authenticatedUserService;
        this.cuentaUsuarioRepository = cuentaUsuarioRepository;
        this.trabajadorRepository = trabajadorRepository;
    }

    public PerfilUsuarioResponse obtenerPerfilActual() {
        return toResponse(authenticatedUserService.currentAccount());
    }

    @Transactional
    public PerfilUsuarioResponse actualizarPerfilActual(ActualizarPerfilRequest request) {
        CuentaUsuario cuenta = authenticatedUserService.currentAccount();

        cuentaUsuarioRepository.findByEmail(request.email())
                .filter(existente -> !existente.getIdCuenta().equals(cuenta.getIdCuenta()))
                .ifPresent(existente -> {
                    throw new RuntimeException("Ya existe otra cuenta con ese email");
                });

        Trabajador trabajador = cuenta.getTrabajador();
        trabajador.setNombres(request.nombres().trim());
        trabajador.setApellidos(request.apellidos().trim());
        trabajador.setTelefono(normalizarTextoOpcional(request.telefono()));
        trabajador.setDireccion(normalizarTextoOpcional(request.direccion()));

        cuenta.setEmail(request.email().trim());

        trabajadorRepository.save(trabajador);
        cuentaUsuarioRepository.save(cuenta);

        return toResponse(cuenta);
    }

    private PerfilUsuarioResponse toResponse(CuentaUsuario cuenta) {
        Trabajador trabajador = cuenta.getTrabajador();
        return new PerfilUsuarioResponse(
                cuenta.getIdCuenta(),
                cuenta.getUsuario(),
                cuenta.getEmail(),
                trabajador.getIdTrabajador(),
                trabajador.getNombres(),
                trabajador.getApellidos(),
                trabajador.getDni(),
                trabajador.getTelefono(),
                trabajador.getDireccion(),
                trabajador.getRol().getNombreRol()
        );
    }

    private String normalizarTextoOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
