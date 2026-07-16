package com.farmape.ms.auth.application.service;

import com.farmape.ms.auth.domain.model.Rol;
import com.farmape.ms.auth.domain.repository.RolRepository;
import com.farmape.ms.auth.domain.model.EstadoTrabajador;
import com.farmape.ms.auth.domain.model.Trabajador;
import com.farmape.ms.auth.domain.repository.TrabajadorRepository;
import com.farmape.ms.auth.api.dto.ActualizarUsuarioRequest;
import com.farmape.ms.auth.api.dto.CambiarClaveUsuarioRequest;
import com.farmape.ms.auth.api.dto.CambiarEstadoCuentaRequest;
import com.farmape.ms.auth.api.dto.CrearUsuarioRequest;
import com.farmape.ms.auth.api.dto.CuentaUsuarioResponse;
import com.farmape.ms.auth.domain.model.EstadoCuentaUsuario;
import com.farmape.ms.auth.domain.model.CuentaUsuario;
import com.farmape.ms.auth.domain.repository.CuentaUsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    @Transactional
    public CuentaUsuarioResponse cambiarEstado(Integer id, CambiarEstadoCuentaRequest request) {
        CuentaUsuario cuenta = cuentaUsuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta de usuario no encontrada"));

        cuenta.setEstado(request.estado());

        sincronizarEstadoTrabajador(cuenta);
        trabajadorRepository.save(cuenta.getTrabajador());

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

        Rol rol = obtenerRolActivo(request.idRol());

        Trabajador trabajador = Trabajador.builder()
                .dni(limpiar(request.dni()))
                .nombres(limpiar(request.nombres()))
                .apellidos(limpiar(request.apellidos()))
                .telefono(limpiar(request.telefono()))
                .direccion(limpiar(request.direccion()))
                .rol(rol)
                .estado(EstadoTrabajador.Activo)
                .fechaRegistro(LocalDateTime.now())
                .build();

        trabajador = trabajadorRepository.save(trabajador);

        CuentaUsuario cuenta = CuentaUsuario.builder()
                .trabajador(trabajador)
                .usuario(limpiar(request.usuario()))
                .email(limpiar(request.email()))
                .clave(passwordEncoder.encode(request.clave()))
                .estado(request.estado() != null ? request.estado() : EstadoCuentaUsuario.Activo)
                .fechaCreacion(LocalDateTime.now())
                .build();

        cuenta = cuentaUsuarioRepository.save(cuenta);

        return toResponse(cuenta);
    }

    @Transactional
    public CuentaUsuarioResponse actualizar(Integer id, ActualizarUsuarioRequest request) {
        CuentaUsuario cuenta = cuentaUsuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta de usuario no encontrada"));

        if (trabajadorRepository.existsByDniAndIdTrabajadorNot(request.dni(), cuenta.getTrabajador().getIdTrabajador())) {
            throw new RuntimeException("Ya existe otro trabajador con ese DNI");
        }

        if (cuentaUsuarioRepository.existsByUsuarioAndIdCuentaNot(request.usuario(), id)) {
            throw new RuntimeException("Ya existe otra cuenta con ese usuario");
        }

        if (cuentaUsuarioRepository.existsByEmailAndIdCuentaNot(request.email(), id)) {
            throw new RuntimeException("Ya existe otra cuenta con ese email");
        }

        Rol rol = obtenerRolActivo(request.idRol());
        Trabajador trabajador = cuenta.getTrabajador();

        trabajador.setDni(limpiar(request.dni()));
        trabajador.setNombres(limpiar(request.nombres()));
        trabajador.setApellidos(limpiar(request.apellidos()));
        trabajador.setTelefono(limpiar(request.telefono()));
        trabajador.setDireccion(limpiar(request.direccion()));
        trabajador.setRol(rol);

        cuenta.setUsuario(limpiar(request.usuario()));
        cuenta.setEmail(limpiar(request.email()));
        cuenta.setEstado(request.estado() != null ? request.estado() : cuenta.getEstado());

        if (request.nuevaClave() != null && !request.nuevaClave().isBlank()) {
            cuenta.setClave(passwordEncoder.encode(request.nuevaClave()));
        }

        sincronizarEstadoTrabajador(cuenta);
        trabajadorRepository.save(trabajador);

        return toResponse(cuentaUsuarioRepository.save(cuenta));
    }

    @Transactional
    public CuentaUsuarioResponse cambiarClaveAdministrativa(Integer id, CambiarClaveUsuarioRequest request) {
        CuentaUsuario cuenta = cuentaUsuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta de usuario no encontrada"));

        cuenta.setClave(passwordEncoder.encode(request.nuevaClave()));
        return toResponse(cuentaUsuarioRepository.save(cuenta));
    }

    private Rol obtenerRolActivo(Integer idRol) {
        return rolRepository.findById(idRol)
                .filter(item -> Boolean.TRUE.equals(item.getActivo()))
                .orElseThrow(() -> new RuntimeException("Rol activo no encontrado"));
    }

    private void sincronizarEstadoTrabajador(CuentaUsuario cuenta) {
        if (cuenta.getEstado() == EstadoCuentaUsuario.Inactivo) {
            cuenta.getTrabajador().setEstado(EstadoTrabajador.Inactivo);
        } else if (cuenta.getEstado() == EstadoCuentaUsuario.Activo) {
            cuenta.getTrabajador().setEstado(EstadoTrabajador.Activo);
        }
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
                cuenta.getTrabajador().getDireccion(),
                cuenta.getTrabajador().getEstado(),
                cuenta.getTrabajador().getRol().getIdRol(),
                cuenta.getTrabajador().getRol().getNombreRol()
        );
    }

    private String limpiar(String valor) {
        if (valor == null) {
            return null;
        }
        String limpio = valor.trim();
        return limpio.isBlank() ? null : limpio;
    }
}
