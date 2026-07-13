package com.farmape.ms.ventas.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VentaNotFoundException extends RuntimeException {

    public VentaNotFoundException(String message) {
        super(message);
    }
}
