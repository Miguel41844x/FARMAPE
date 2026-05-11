package com.farmape.backend.auth.dto;

public record LoginResponse(
        String token,
        String email,
        String rol,
        String nombres,
        String apellidos
) {}