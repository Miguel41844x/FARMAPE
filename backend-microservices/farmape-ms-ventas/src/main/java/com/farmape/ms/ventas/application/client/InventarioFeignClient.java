package com.farmape.ms.ventas.application.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "farmape-ms-inventario",
        url = "${farmape.inventario.base-url:http://localhost:8081}",
        path = "/api"
)
public interface InventarioFeignClient {

    @GetMapping("/productos/{idProducto}")
    InventarioProductoResponse obtenerProducto(@PathVariable Integer idProducto);

    @PostMapping("/inventario/movimientos")
    void registrarMovimiento(@RequestBody InventarioMovimientoRequest request);
}
