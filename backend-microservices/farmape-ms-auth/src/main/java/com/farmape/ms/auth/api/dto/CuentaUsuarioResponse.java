package com.farmape.ms.auth.usuarios.dto;

import com.farmape.ms.auth.trabajadores.enums.EstadoTrabajador;
import com.farmape.ms.auth.usuarios.enums.EstadoCuentaUsuario;

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
