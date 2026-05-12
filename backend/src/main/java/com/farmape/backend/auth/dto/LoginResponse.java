package com.farmape.backend.auth.dto;

public record LoginResponse(
        String token,
        String usuario,
        String rol,
        String nombres,
        String apellidos
) {}