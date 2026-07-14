package com.farmape.ms.auth.application.exception;

public class AuthBusinessException extends RuntimeException {
    public AuthBusinessException(String message) {
        super(message);
    }
}