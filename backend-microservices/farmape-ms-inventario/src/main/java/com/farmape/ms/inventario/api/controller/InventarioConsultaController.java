package com.farmape.ms.inventario.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.farmape.ms.inventario.api.dto.CategoriaResponse;
import com.farmape.ms.inventario.api.dto.LoteProductoResponse;
import com.farmape.ms.inventario.api.dto.MovimientoAlmacenResponse;
import com.farmape.ms.inventario.api.dto.ProductoResponse;
import com.farmape.ms.inventario.application.service.InventarioConsultaService;

@RestController
@RequestMapping("/api/inventario")
public class InventarioConsultaController {

    private final InventarioConsultaService inventarioConsultaService;

    public InventarioConsultaController(InventarioConsultaService inventarioConsultaService) {
        this.inventarioConsultaService = inventarioConsultaService;
    }

    @GetMapping("/categorias")
    public List<CategoriaResponse> listarCategoriasActivas() {
        return inventarioConsultaService.listarCategoriasActivas();
    }

    @GetMapping("/productos")
    public List<ProductoResponse> listarProductosActivos() {
        return inventarioConsultaService.listarProductosActivos();
    }

    @GetMapping("/productos/buscar")
    public List<ProductoResponse> buscarProductos(@RequestParam(required = false) String nombre) {
        return inventarioConsultaService.buscarProductos(nombre);
    }

    @GetMapping("/productos/stock-bajo")
    public List<ProductoResponse> listarProductosConStockBajo() {
        return inventarioConsultaService.listarProductosConStockBajo();
    }

    @GetMapping("/productos/{idProducto}")
    public ProductoResponse obtenerProducto(@PathVariable Integer idProducto) {
        return inventarioConsultaService.obtenerProducto(idProducto);
    }

    @GetMapping("/productos/{idProducto}/lotes")
    public List<LoteProductoResponse> listarLotesPorProducto(@PathVariable Integer idProducto) {
        return inventarioConsultaService.listarLotesPorProducto(idProducto);
    }

    @GetMapping("/movimientos")
    public List<MovimientoAlmacenResponse> listarMovimientosRecientes() {
        return inventarioConsultaService.listarMovimientosRecientes();
    }

    @GetMapping("/productos/{idProducto}/movimientos")
    public List<MovimientoAlmacenResponse> listarMovimientosPorProducto(@PathVariable Integer idProducto) {
        return inventarioConsultaService.listarMovimientosPorProducto(idProducto);
    }
}
