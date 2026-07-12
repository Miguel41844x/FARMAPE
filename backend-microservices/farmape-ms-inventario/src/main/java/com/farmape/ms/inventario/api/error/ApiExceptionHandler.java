package com.farmape.ms.inventario.api.error;

import java.time.Instant;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.farmape.ms.inventario.application.exception.InventarioBusinessException;
import com.farmape.ms.inventario.application.exception.InventarioNotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(InventarioBusinessException.class)
    public ResponseEntity<ApiError> business(InventarioBusinessException exception) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(InventarioNotFoundException.class)
    public ResponseEntity<ApiError> notFound(InventarioNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> dataIntegrity() {
        return response(HttpStatus.CONFLICT, "No se puede completar la operacion porque afecta datos relacionados.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> invalidBody() {
        return response(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud no tiene un formato valido.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> typeMismatch(MethodArgumentTypeMismatchException exception) {
        return response(HttpStatus.BAD_REQUEST, "Parametro invalido: " + exception.getName());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> methodNotAllowed(HttpRequestMethodNotSupportedException exception) {
        String method = exception.getMethod() == null ? "HTTP" : exception.getMethod();
        return response(HttpStatus.METHOD_NOT_ALLOWED, "Metodo " + method + " no permitido para este recurso.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> runtime(RuntimeException exception) {
        String message = exception.getMessage() == null ? "No se pudo procesar la solicitud." : exception.getMessage();
        return response(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    private ResponseEntity<ApiError> response(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ApiError(status.value(), message, Instant.now()));
    }

    public record ApiError(int status, String message, Instant timestamp) {
    }
}
