package com.farmape.ms.inventario.api.error;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import com.farmape.ms.inventario.application.exception.InventarioBusinessException;
import com.farmape.ms.inventario.application.exception.InventarioNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTests {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void businessExceptionDevuelveBadRequest() {
        var response = handler.business(new InventarioBusinessException("La cantidad debe ser mayor que cero."));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("La cantidad debe ser mayor que cero.");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void notFoundExceptionDevuelveNotFound() {
        var response = handler.notFound(new InventarioNotFoundException("Producto no encontrado: 99"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().message()).isEqualTo("Producto no encontrado: 99");
    }

    @Test
    void methodNotAllowedDevuelveJsonConMensajeClaro() {
        var response = handler.methodNotAllowed(new HttpRequestMethodNotSupportedException("GET"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(405);
        assertThat(response.getBody().message()).isEqualTo("Metodo GET no permitido para este recurso.");
    }
}
