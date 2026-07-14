package com.farmape.ms.ventas.application.client;

public interface InventarioClient {

    InventarioProductoResponse obtenerProducto(Integer idProducto);

    void reducirStock(Integer idProducto, Integer cantidad, Integer idVenta);

    void restaurarStock(Integer idProducto, Integer cantidad, Integer idVenta);
}
