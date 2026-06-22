package com.farmape.backend.usuarios.dto;

import com.farmape.backend.usuarios.enums.EstadoCuentaUsuario;
import com.farmape.backend.trabajadores.enums.EstadoTrabajador;

import java.time.LocalDateTime;

public record CuentaUsuarioResponse(
        Integer idCuenta,
        String usuario,
        String email,
        EstadoCuentaUsuario estado,
        LocalDateTime ultimoAcceso,
        LocalDateTime fechaCreacion,
        Integer idTrabajador,
        String nombres,
        String apellidos,
        String dni,
        String telefono,
        String direccion,
        EstadoTrabajador estadoTrabajador,
        Integer idRol,
        String rol
) {
}
