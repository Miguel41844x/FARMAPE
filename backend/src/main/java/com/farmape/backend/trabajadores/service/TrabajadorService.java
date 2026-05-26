package com.farmape.backend.trabajadores.service;

import com.farmape.backend.roles.model.Rol;
import com.farmape.backend.roles.repository.RolRepository;
import com.farmape.backend.trabajadores.dto.TrabajadorRequest;
import com.farmape.backend.trabajadores.dto.TrabajadorResponse;
import com.farmape.backend.trabajadores.enums.EstadoTrabajador;
import com.farmape.backend.trabajadores.model.Trabajador;
import com.farmape.backend.trabajadores.repository.TrabajadorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrabajadorService {

    private final TrabajadorRepository trabajadorRepository;
    private final RolRepository rolRepository;

    public TrabajadorService(
            TrabajadorRepository trabajadorRepository,
            RolRepository rolRepository
    ) {
        this.trabajadorRepository = trabajadorRepository;
        this.rolRepository = rolRepository;
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

    public TrabajadorResponse cambiarEstado(Integer id, EstadoTrabajador estado) {
        Trabajador trabajador = trabajadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trabajador no encontrado"));

        trabajador.setEstado(estado);

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