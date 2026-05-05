package com.farmape.backend.dto.auth;

public record LoginResponse(
        String token,
        String email,
        String rol,
        String nombres,
        String apellidos
) {}