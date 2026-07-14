package com.farmape.ms.ventas.application.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.farmape.ms.ventas.application.exception.VentaBusinessException;
import com.farmape.ms.ventas.application.exception.VentaIntegrationException;

import feign.FeignException;

@Component
public class InventarioFeignAdapter implements InventarioClient {

    private static final String REFERENCIA_VENTA = "VENTA";
    private static final String MOTIVO_VENTA = "Venta";
    private static final String TIPO_ENTRADA = "Entrada";
    private static final String TIPO_SALIDA = "Salida";

    private final InventarioFeignClient inventarioFeignClient;
    private final Integer idTrabajadorSistema;

    public InventarioFeignAdapter(
            InventarioFeignClient inventarioFeignClient,
            @Value("${farmape.ventas.id-trabajador-sistema:1}") Integer idTrabajadorSistema
    ) {
        this.inventarioFeignClient = inventarioFeignClient;
        this.idTrabajadorSistema = idTrabajadorSistema;
    }

    @Override
    public InventarioProductoResponse obtenerProducto(Integer idProducto) {
        try {
            return inventarioFeignClient.obtenerProducto(idProducto);
        } catch (FeignException.NotFound exception) {
            throw new VentaBusinessException("Producto no encontrado: " + idProducto);
        } catch (FeignException exception) {
            throw new VentaIntegrationException("No se pudo consultar el producto " + idProducto + ".", exception);
        } catch (RuntimeException exception) {
            throw new VentaIntegrationException("No se pudo comunicar con el microservicio de inventario.", exception);
        }
    }

    @Override
    public void reducirStock(Integer idProducto, Integer cantidad, Integer idVenta) {
        registrarMovimiento(idProducto, cantidad, TIPO_SALIDA, idVenta);
    }

    @Override
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
            inventarioFeignClient.registrarMovimiento(request);
        } catch (FeignException exception) {
            throw new VentaIntegrationException("No se pudo actualizar el stock del producto " + idProducto + ".", exception);
        } catch (RuntimeException exception) {
            throw new VentaIntegrationException("No se pudo comunicar con el microservicio de inventario.", exception);
        }
    }
}
