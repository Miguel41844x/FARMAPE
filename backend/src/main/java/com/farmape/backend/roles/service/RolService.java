package com.farmape.backend.roles.service;

import com.farmape.backend.roles.dto.*;
import com.farmape.backend.roles.model.Permiso;
import com.farmape.backend.roles.model.Rol;
import com.farmape.backend.roles.repository.PermisoRepository;
import com.farmape.backend.roles.repository.RolRepository;
import com.farmape.backend.trabajadores.repository.TrabajadorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RolService {

    private static final Set<String> ROLES_BASE = Set.of(
            "ADMIN", "EMPLOYEE", "CASHIER", "DISPATCH",
            "WAREHOUSE", "PHARMACIST", "MANAGER"
    );

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final TrabajadorRepository trabajadorRepository;

    public RolService(
            RolRepository rolRepository,
            PermisoRepository permisoRepository,
            TrabajadorRepository trabajadorRepository
    ) {
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
        this.trabajadorRepository = trabajadorRepository;
    }

    public List<RolResponse> listarRoles(boolean incluirInactivos) {
        return rolRepository.findAll().stream()
                .filter(rol -> incluirInactivos || Boolean.TRUE.equals(rol.getActivo()))
                .sorted(Comparator.comparing(Rol::getNombreRol))
                .map(this::toResponse)
                .toList();
    }

    public List<PermisoResponse> listarPermisos() {
        return permisoRepository.findAllByActivoTrueOrderByModuloAscNombreAsc().stream()
                .map(permiso -> new PermisoResponse(
                        permiso.getIdPermiso(), permiso.getCodigo(), permiso.getNombre(),
                        permiso.getModulo(), permiso.getActivo()
                ))
                .toList();
    }

    @Transactional
    public RolResponse crear(RolRequest request) {
        String codigo = normalizarCodigo(request.codigo());
        if (rolRepository.findByCodigo(codigo).isPresent()) {
            throw new IllegalArgumentException("Ya existe un rol con ese código");
        }
        if (rolRepository.findByNombreRol(request.nombreRol().trim()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un rol con ese nombre");
        }

        Rol rol = Rol.builder()
                .codigo(codigo)
                .nombreRol(request.nombreRol().trim())
                .descripcion(request.descripcion())
                .activo(true)
                .build();
        asignarPermisosSiLlegan(rol, request.idPermisos());
        return toResponse(rolRepository.save(rol));
    }

    @Transactional
    public RolResponse actualizar(Integer idRol, RolRequest request) {
        Rol rol = obtener(idRol);
        String codigo = normalizarCodigo(request.codigo());
        if (esRolBase(rol) && !rol.getCodigo().equals(codigo)) {
            throw new IllegalStateException("No se puede cambiar el código de un rol base");
        }

        rolRepository.findByCodigo(codigo)
                .filter(existente -> !existente.getIdRol().equals(idRol))
                .ifPresent(existente -> { throw new IllegalArgumentException("Ya existe un rol con ese código"); });
        rolRepository.findByNombreRol(request.nombreRol().trim())
                .filter(existente -> !existente.getIdRol().equals(idRol))
                .ifPresent(existente -> { throw new IllegalArgumentException("Ya existe un rol con ese nombre"); });

        rol.setCodigo(codigo);
        rol.setNombreRol(request.nombreRol().trim());
        rol.setDescripcion(request.descripcion());
        asignarPermisosSiLlegan(rol, request.idPermisos());
        return toResponse(rolRepository.save(rol));
    }

    @Transactional
    public RolResponse asignarPermisos(Integer idRol, AsignarPermisosRequest request) {
        Rol rol = obtener(idRol);
        Set<Integer> ids = request.idPermisos() == null ? Set.of() : request.idPermisos();
        List<Permiso> permisos = permisoRepository.findAllById(ids).stream()
                .filter(permiso -> Boolean.TRUE.equals(permiso.getActivo()))
                .toList();

        if (permisos.size() != ids.size()) {
            throw new IllegalArgumentException("Uno o más permisos no existen o están inactivos");
        }

        rol.setPermisos(new LinkedHashSet<>(permisos));
        return toResponse(rolRepository.save(rol));
    }

    @Transactional
    public RolResponse cambiarEstado(Integer idRol, EstadoRolRequest request) {
        Rol rol = obtener(idRol);
        if (!request.activo() && esRolBase(rol)) {
            throw new IllegalStateException("No se puede desactivar un rol base del sistema");
        }
        if (!request.activo() && trabajadorRepository.countByRol_IdRol(idRol) > 0) {
            throw new IllegalStateException("Reasigna los trabajadores antes de desactivar este rol");
        }
        rol.setActivo(request.activo());
        return toResponse(rolRepository.save(rol));
    }

    @Transactional
    public void eliminar(Integer idRol) {
        Rol rol = obtener(idRol);
        if (esRolBase(rol)) {
            throw new IllegalStateException("No se puede eliminar un rol base del sistema");
        }
        if (trabajadorRepository.countByRol_IdRol(idRol) > 0) {
            throw new IllegalStateException("No se puede eliminar un rol asignado a trabajadores");
        }
        rolRepository.delete(rol);
    }


    private void asignarPermisosSiLlegan(Rol rol, Set<Integer> idPermisos) {
        if (idPermisos == null) {
            return;
        }
        List<Permiso> permisos = permisoRepository.findAllById(idPermisos).stream()
                .filter(permiso -> Boolean.TRUE.equals(permiso.getActivo()))
                .toList();
        Set<Integer> encontrados = permisos.stream()
                .map(Permiso::getIdPermiso)
                .collect(Collectors.toSet());
        if (!encontrados.containsAll(idPermisos)) {
            throw new IllegalArgumentException("Uno o más permisos no existen o están inactivos");
        }
        rol.setPermisos(new LinkedHashSet<>(permisos));
    }

    private boolean esRolBase(Rol rol) {
        return rol.getCodigo() != null && ROLES_BASE.contains(rol.getCodigo());
    }

    private Rol obtener(Integer idRol) {
        return rolRepository.findById(idRol)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
    }

    private String normalizarCodigo(String codigo) {
        return codigo.trim().toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
    }

    private RolResponse toResponse(Rol rol) {
        List<Permiso> permisos = rol.getPermisos().stream()
                .filter(permiso -> Boolean.TRUE.equals(permiso.getActivo()))
                .sorted(Comparator.comparing(Permiso::getModulo).thenComparing(Permiso::getNombre))
                .toList();
        return new RolResponse(
                rol.getIdRol(), rol.getCodigo(), rol.getNombreRol(), rol.getDescripcion(), rol.getActivo(),
                permisos.stream().map(Permiso::getIdPermiso).toList(),
                permisos.stream().map(Permiso::getCodigo).toList()
        );
    }
}
