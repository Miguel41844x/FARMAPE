package com.farmape.ms.ventas.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class VentaIntegrationException extends RuntimeException {

    public VentaIntegrationException(String message) {
        super(message);
    }

    public VentaIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
