package com.farmape.backend.usuarios.service;

import com.farmape.backend.usuarios.dto.CambiarEstadoCuentaRequest;
import com.farmape.backend.usuarios.dto.CuentaUsuarioResponse;
import com.farmape.backend.usuarios.model.CuentaUsuario;
import com.farmape.backend.usuarios.repository.CuentaUsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuentaUsuarioService {

    private final CuentaUsuarioRepository cuentaUsuarioRepository;

    public CuentaUsuarioService(CuentaUsuarioRepository cuentaUsuarioRepository) {
        this.cuentaUsuarioRepository = cuentaUsuarioRepository;
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

    private CuentaUsuarioResponse toResponse(CuentaUsuario cuenta) {
        return new CuentaUsuarioResponse(
                cuenta.getIdCuenta(),
                cuenta.getUsuario(),
                cuenta.getEstado(),
                cuenta.getUltimoAcceso(),
                cuenta.getFechaCreacion(),
                cuenta.getTrabajador().getIdTrabajador(),
                cuenta.getTrabajador().getNombres(),
                cuenta.getTrabajador().getApellidos(),
                cuenta.getTrabajador().getRol().getNombreRol()
        );
    }
}