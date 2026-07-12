package com.farmape.ms.inventario.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.farmape.ms.inventario.api.dto.CategoriaResponse;
import com.farmape.ms.inventario.api.dto.InformeAlmacenResponse;
import com.farmape.ms.inventario.api.dto.IngresoAlmacenRequest;
import com.farmape.ms.inventario.api.dto.IngresoAlmacenResponse;
import com.farmape.ms.inventario.api.dto.LoteProductoResponse;
import com.farmape.ms.inventario.api.dto.MovimientoAlmacenResponse;
import com.farmape.ms.inventario.api.dto.ProductoEstadoRequest;
import com.farmape.ms.inventario.api.dto.ProductoRequest;
import com.farmape.ms.inventario.api.dto.ProductoResponse;
import com.farmape.ms.inventario.api.dto.ResumenInventarioResponse;
import com.farmape.ms.inventario.api.dto.VerificacionProductoResponse;
import com.farmape.ms.inventario.application.service.InventarioConsultaService;

@RestController
@RequestMapping({"/api/inventario", "/api"})
public class InventarioConsultaController {

    private final InventarioConsultaService inventarioConsultaService;

    public InventarioConsultaController(InventarioConsultaService inventarioConsultaService) {
        this.inventarioConsultaService = inventarioConsultaService;
    }

    @GetMapping("/categorias")
    public List<CategoriaResponse> listarCategoriasActivas() {
        return inventarioConsultaService.listarCategoriasActivas();
    }

    @GetMapping("/resumen")
    public ResumenInventarioResponse obtenerResumen() {
        return inventarioConsultaService.obtenerResumen();
    }

    @GetMapping("/productos")
    public List<ProductoResponse> listarProductos() {
        return inventarioConsultaService.listarProductos();
    }

    @GetMapping("/productos/activos")
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

    @PostMapping("/productos")
    public ProductoResponse crearProducto(@RequestBody ProductoRequest request) {
        return inventarioConsultaService.crearProducto(request);
    }

    @PutMapping("/productos/{idProducto}")
    public ProductoResponse actualizarProducto(
            @PathVariable Integer idProducto,
            @RequestBody ProductoRequest request
    ) {
        return inventarioConsultaService.actualizarProducto(idProducto, request);
    }

    @PatchMapping("/productos/{idProducto}/estado")
    public ProductoResponse cambiarEstadoProducto(
            @PathVariable Integer idProducto,
            @RequestBody ProductoEstadoRequest request
    ) {
        return inventarioConsultaService.cambiarEstadoProducto(idProducto, request);
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

    @GetMapping("/almacen/ingresos")
    public List<IngresoAlmacenResponse> listarIngresosAlmacen() {
        return inventarioConsultaService.listarIngresosAlmacen();
    }

    @PostMapping("/almacen/ingresos")
    public IngresoAlmacenResponse registrarIngresoAlmacen(@RequestBody IngresoAlmacenRequest request) {
        return inventarioConsultaService.registrarIngresoAlmacen(request);
    }

    @GetMapping("/almacen/verificaciones")
    public List<VerificacionProductoResponse> listarVerificacionesProductos() {
        return inventarioConsultaService.listarVerificacionesProductos();
    }

    @PatchMapping("/almacen/verificaciones/{idVerificacion}/confirmar")
    public VerificacionProductoResponse confirmarVerificacionProducto(@PathVariable Integer idVerificacion) {
        return inventarioConsultaService.confirmarVerificacionProducto(idVerificacion);
    }

    @PatchMapping("/almacen/verificaciones/{idVerificacion}/observar")
    public VerificacionProductoResponse observarVerificacionProducto(@PathVariable Integer idVerificacion) {
        return inventarioConsultaService.observarVerificacionProducto(idVerificacion);
    }

    @GetMapping("/almacen/informe")
    public List<InformeAlmacenResponse> obtenerInformeAlmacen(
            @RequestParam(defaultValue = "HOY") String periodo
    ) {
        return inventarioConsultaService.obtenerInformeAlmacen(periodo);
    }
}
