package com.farmape.backend.perfil.dto;

public record PerfilUsuarioResponse(
        Integer idCuenta,
        String usuario,
        String email,
        Integer idTrabajador,
        String nombres,
        String apellidos,
        String dni,
        String telefono,
        String direccion,
        String rol
) {}
