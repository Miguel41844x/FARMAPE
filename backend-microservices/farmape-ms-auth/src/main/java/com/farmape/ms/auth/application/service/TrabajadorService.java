package com.farmape.ms.auth.application.service;

import com.farmape.ms.auth.domain.model.Rol;
import com.farmape.ms.auth.domain.repository.RolRepository;
import com.farmape.ms.auth.api.dto.TrabajadorRequest;
import com.farmape.ms.auth.api.dto.TrabajadorResponse;
import com.farmape.ms.auth.domain.model.EstadoTrabajador;
import com.farmape.ms.auth.domain.model.Trabajador;
import com.farmape.ms.auth.domain.repository.TrabajadorRepository;
import com.farmape.ms.auth.domain.model.EstadoCuentaUsuario;
import com.farmape.ms.auth.domain.repository.CuentaUsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrabajadorService {

    private final TrabajadorRepository trabajadorRepository;
    private final RolRepository rolRepository;
    private final CuentaUsuarioRepository cuentaUsuarioRepository;

    public TrabajadorService(
            TrabajadorRepository trabajadorRepository,
            RolRepository rolRepository,
            CuentaUsuarioRepository cuentaUsuarioRepository
    ) {
        this.trabajadorRepository = trabajadorRepository;
        this.rolRepository = rolRepository;
        this.cuentaUsuarioRepository = cuentaUsuarioRepository;
    }

    public List<TrabajadorResponse> listar() {
        return trabajadorRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TrabajadorResponse obtenerPorId(Integer id) {
        Trabajador trabajador = trabajadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trabajador no encontrado"));

        return toResponse(trabajador);
    }

    public TrabajadorResponse crear(TrabajadorRequest request) {
        Rol rol = rolRepository.findById(request.idRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Trabajador trabajador = Trabajador.builder()
                .dni(request.dni())
                .nombres(request.nombres())
                .apellidos(request.apellidos())
                .telefono(request.telefono())
                .direccion(request.direccion())
                .rol(rol)
                .estado(request.estado() != null ? request.estado() : EstadoTrabajador.Activo)
                .fechaRegistro(LocalDateTime.now())
                .build();

        return toResponse(trabajadorRepository.save(trabajador));
    }

    public TrabajadorResponse actualizar(Integer id, TrabajadorRequest request) {
        Trabajador trabajador = trabajadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trabajador no encontrado"));

        Rol rol = rolRepository.findById(request.idRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        trabajador.setDni(request.dni());
        trabajador.setNombres(request.nombres());
        trabajador.setApellidos(request.apellidos());
        trabajador.setTelefono(request.telefono());
        trabajador.setDireccion(request.direccion());
        trabajador.setRol(rol);

        if (request.estado() != null) {
            trabajador.setEstado(request.estado());
        }

        return toResponse(trabajadorRepository.save(trabajador));
    }

    @Transactional
    public TrabajadorResponse cambiarEstado(Integer id, EstadoTrabajador estado) {
        Trabajador trabajador = trabajadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trabajador no encontrado"));

        trabajador.setEstado(estado);

        cuentaUsuarioRepository.findByTrabajador_IdTrabajador(id).ifPresent(cuenta -> {
            if (estado == EstadoTrabajador.Inactivo) {
                cuenta.setEstado(EstadoCuentaUsuario.Inactivo);
            } else if (cuenta.getEstado() == EstadoCuentaUsuario.Inactivo) {
                cuenta.setEstado(EstadoCuentaUsuario.Activo);
            }
            cuentaUsuarioRepository.save(cuenta);
        });

        return toResponse(trabajadorRepository.save(trabajador));
    }

    private TrabajadorResponse toResponse(Trabajador trabajador) {
        return new TrabajadorResponse(
                trabajador.getIdTrabajador(),
                trabajador.getDni(),
                trabajador.getNombres(),
                trabajador.getApellidos(),
                trabajador.getTelefono(),
                trabajador.getDireccion(),
                trabajador.getRol().getIdRol(),
                trabajador.getRol().getNombreRol(),
                trabajador.getEstado()
        );
    }
}
