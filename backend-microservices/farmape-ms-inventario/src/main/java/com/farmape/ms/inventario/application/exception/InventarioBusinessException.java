package com.farmape.ms.inventario.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InventarioBusinessException extends RuntimeException {

    public InventarioBusinessException(String message) {
        super(message);
    }
}
