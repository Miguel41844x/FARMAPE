package com.farmape.backend.usuarios.service;

import com.farmape.backend.roles.model.Rol;
import com.farmape.backend.roles.repository.RolRepository;
import com.farmape.backend.trabajadores.enums.EstadoTrabajador;
import com.farmape.backend.trabajadores.model.Trabajador;
import com.farmape.backend.trabajadores.repository.TrabajadorRepository;
import com.farmape.backend.usuarios.dto.CambiarEstadoCuentaRequest;
import com.farmape.backend.usuarios.dto.CrearUsuarioRequest;
import com.farmape.backend.usuarios.dto.CuentaUsuarioResponse;
import com.farmape.backend.usuarios.enums.EstadoCuentaUsuario;
import com.farmape.backend.usuarios.model.CuentaUsuario;
import com.farmape.backend.usuarios.repository.CuentaUsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CuentaUsuarioService {

    private final CuentaUsuarioRepository cuentaUsuarioRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public CuentaUsuarioService(
            CuentaUsuarioRepository cuentaUsuarioRepository,
            TrabajadorRepository trabajadorRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.cuentaUsuarioRepository = cuentaUsuarioRepository;
        this.trabajadorRepository = trabajadorRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<CuentaUsuarioResponse> listarUsuarios() {
        return cuentaUsuarioRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CuentaUsuarioResponse obtenerPorId(Integer id) {
        CuentaUsuario cuenta = cuentaUsuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta de usuario no encontrada"));

        return toResponse(cuenta);
    }

    public CuentaUsuarioResponse cambiarEstado(Integer id, CambiarEstadoCuentaRequest request) {
        CuentaUsuario cuenta = cuentaUsuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta de usuario no encontrada"));

        cuenta.setEstado(request.estado());

        CuentaUsuario cuentaActualizada = cuentaUsuarioRepository.save(cuenta);

        return toResponse(cuentaActualizada);
    }

    @Transactional
    public CuentaUsuarioResponse crear(CrearUsuarioRequest request) {
        if (trabajadorRepository.existsByDni(request.dni())) {
            throw new RuntimeException("Ya existe un trabajador con ese DNI");
        }

        if (cuentaUsuarioRepository.existsByUsuario(request.usuario())) {
            throw new RuntimeException("Ya existe una cuenta con ese usuario");
        }

        if (cuentaUsuarioRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Ya existe una cuenta con ese email");
        }

        Rol rol = rolRepository.findByNombreRol(request.rol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + request.rol()));

            Trabajador trabajador = Trabajador.builder()
                    .dni(request.dni())
                    .nombres(request.nombres())
                    .apellidos(request.apellidos())
                    .telefono(request.telefono())
                    .direccion(request.direccion())
                    .rol(rol)
                    .estado(EstadoTrabajador.Activo)
                    .build();

            trabajador = trabajadorRepository.save(trabajador);

            CuentaUsuario cuenta = CuentaUsuario.builder()
                    .trabajador(trabajador)
                    .usuario(request.usuario())
                    .email(request.email())
                    .clave(passwordEncoder.encode(request.clave()))
                    .estado(request.estado() != null ? request.estado() : EstadoCuentaUsuario.Activo)
                    .fechaCreacion(LocalDateTime.now())
                    .build();

            cuenta = cuentaUsuarioRepository.save(cuenta);

            return toResponse(cuenta);
        }

    private CuentaUsuarioResponse toResponse(CuentaUsuario cuenta) {
        return new CuentaUsuarioResponse(
                cuenta.getIdCuenta(),
                cuenta.getUsuario(),
                cuenta.getEmail(),
                cuenta.getEstado(),
                cuenta.getUltimoAcceso(),
                cuenta.getFechaCreacion(),
                cuenta.getTrabajador().getIdTrabajador(),
                cuenta.getTrabajador().getNombres(),
                cuenta.getTrabajador().getApellidos(),
                cuenta.getTrabajador().getDni(),
                cuenta.getTrabajador().getTelefono(),
                cuenta.getTrabajador().getRol().getNombreRol()
        );
    }
}