package com.farmape.ms.ventas.application.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.farmape.ms.ventas.application.exception.VentaBusinessException;
import com.farmape.ms.ventas.application.exception.VentaIntegrationException;

@Component
public class InventarioClient {

    private static final String REFERENCIA_VENTA = "VENTA";
    private static final String MOTIVO_VENTA = "Venta";
    private static final String TIPO_ENTRADA = "Entrada";
    private static final String TIPO_SALIDA = "Salida";

    private final RestClient restClient;
    private final Integer idTrabajadorSistema;

    public InventarioClient(
            RestClient.Builder restClientBuilder,
            @Value("${farmape.inventario.base-url:http://localhost:8081}") String inventarioBaseUrl,
            @Value("${farmape.ventas.id-trabajador-sistema:1}") Integer idTrabajadorSistema
    ) {
        this.restClient = restClientBuilder.baseUrl(inventarioBaseUrl).build();
        this.idTrabajadorSistema = idTrabajadorSistema;
    }

    public InventarioProductoResponse obtenerProducto(Integer idProducto) {
        try {
            return restClient.get()
                    .uri("/api/productos/{idProducto}", idProducto)
                    .retrieve()
                    .body(InventarioProductoResponse.class);
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new VentaBusinessException("Producto no encontrado: " + idProducto);
            }
            throw new VentaIntegrationException("No se pudo consultar el producto " + idProducto + ".", exception);
        } catch (RestClientException exception) {
            throw new VentaIntegrationException("No se pudo comunicar con el microservicio de inventario.", exception);
        }
    }

    public void reducirStock(Integer idProducto, Integer cantidad, Integer idVenta) {
        registrarMovimiento(idProducto, cantidad, TIPO_SALIDA, idVenta);
    }

    public void restaurarStock(Integer idProducto, Integer cantidad, Integer idVenta) {
        registrarMovimiento(idProducto, cantidad, TIPO_ENTRADA, idVenta);
    }

    private void registrarMovimiento(Integer idProducto, Integer cantidad, String tipoMovimiento, Integer idVenta) {
        InventarioMovimientoRequest request = new InventarioMovimientoRequest(
                idProducto,
                null,
                idTrabajadorSistema,
                tipoMovimiento,
                MOTIVO_VENTA,
                cantidad,
                REFERENCIA_VENTA,
                idVenta,
                "Movimiento generado por venta " + idVenta
        );

        try {
            restClient.post()
                    .uri("/api/inventario/movimientos")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException exception) {
            throw new VentaIntegrationException("No se pudo actualizar el stock del producto " + idProducto + ".", exception);
        } catch (RestClientException exception) {
            throw new VentaIntegrationException("No se pudo comunicar con el microservicio de inventario.", exception);
        }
    }
}
