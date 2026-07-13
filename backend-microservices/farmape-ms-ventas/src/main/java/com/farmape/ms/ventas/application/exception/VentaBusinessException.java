package com.farmape.ms.ventas.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VentaBusinessException extends RuntimeException {

    public VentaBusinessException(String message) {
        super(message);
    }
}
