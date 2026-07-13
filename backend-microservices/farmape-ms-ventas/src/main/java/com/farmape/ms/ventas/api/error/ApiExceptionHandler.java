package com.farmape.ms.ventas.api.error;

import java.time.Instant;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.farmape.ms.ventas.application.exception.VentaBusinessException;
import com.farmape.ms.ventas.application.exception.VentaIntegrationException;
import com.farmape.ms.ventas.application.exception.VentaNotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(VentaBusinessException.class)
    public ResponseEntity<ApiError> business(VentaBusinessException exception) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(VentaNotFoundException.class)
    public ResponseEntity<ApiError> notFound(VentaNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(VentaIntegrationException.class)
    public ResponseEntity<ApiError> integration(VentaIntegrationException exception) {
        return response(HttpStatus.BAD_GATEWAY, exception.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiError> dataAccess() {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo acceder a la base de datos de ventas.");
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
