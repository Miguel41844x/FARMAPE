package com.farmape.backend.auth.dto;

import java.util.List;

public record LoginResponse(
        String token,
        String usuario,
        String rol,
        String nombres,
        String apellidos,
        Integer idCuenta,
        Integer idTrabajador,
        List<String> permisos
) {}
