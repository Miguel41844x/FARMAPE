package com.farmape.ms.inventario.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmape.ms.inventario.api.dto.AjusteInventarioRequest;
import com.farmape.ms.inventario.api.dto.MovimientoAlmacenRequest;
import com.farmape.ms.inventario.api.dto.MovimientoAlmacenResponse;
import com.farmape.ms.inventario.application.exception.InventarioBusinessException;
import com.farmape.ms.inventario.application.exception.InventarioNotFoundException;
import com.farmape.ms.inventario.domain.model.LoteProducto;
import com.farmape.ms.inventario.domain.model.MotivoMovimiento;
import com.farmape.ms.inventario.domain.model.MovimientoAlmacen;
import com.farmape.ms.inventario.domain.model.Producto;
import com.farmape.ms.inventario.domain.model.TipoMovimiento;
import com.farmape.ms.inventario.domain.repository.LoteProductoRepository;
import com.farmape.ms.inventario.domain.repository.MovimientoAlmacenRepository;
import com.farmape.ms.inventario.domain.repository.ProductoRepository;

@Service
public class InventarioMovimientoService {

    private final ProductoRepository productoRepository;
    private final LoteProductoRepository loteProductoRepository;
    private final MovimientoAlmacenRepository movimientoAlmacenRepository;

    public InventarioMovimientoService(
            ProductoRepository productoRepository,
            LoteProductoRepository loteProductoRepository,
            MovimientoAlmacenRepository movimientoAlmacenRepository
    ) {
        this.productoRepository = productoRepository;
        this.loteProductoRepository = loteProductoRepository;
        this.movimientoAlmacenRepository = movimientoAlmacenRepository;
    }

    @Transactional
    public MovimientoAlmacenResponse registrarMovimiento(MovimientoAlmacenRequest request) {
        validarRequest(request);

        TipoMovimiento tipoMovimiento = parseTipoMovimiento(request.tipoMovimiento());
        MotivoMovimiento motivo = parseMotivo(request.motivo());

        if (tipoMovimiento == TipoMovimiento.Ajuste) {
            throw new InventarioBusinessException("El ajuste de inventario se implementara en un flujo separado.");
        }

        Producto producto = productoRepository.findByIdForUpdate(request.idProducto())
                .orElseThrow(() -> new InventarioNotFoundException("Producto no encontrado: " + request.idProducto()));
        LoteProducto lote = obtenerLote(request.idLote(), producto);

        if (tipoMovimiento == TipoMovimiento.Entrada) {
            sumarStock(producto, lote, request.cantidad());
        } else {
            restarStock(producto, lote, request.cantidad());
        }

        MovimientoAlmacen movimiento = new MovimientoAlmacen();
        movimiento.setProducto(producto);
        movimiento.setLote(lote);
        movimiento.setIdTrabajador(request.idTrabajador());
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setMotivo(motivo);
        movimiento.setCantidad(request.cantidad());
        movimiento.setReferenciaTipo(request.referenciaTipo());
        movimiento.setReferenciaId(request.referenciaId());
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setObservacion(request.observacion());

        MovimientoAlmacen guardado = movimientoAlmacenRepository.save(movimiento);

        return toMovimientoAlmacenResponse(guardado);
    }

    @Transactional
    public MovimientoAlmacenResponse registrarAjuste(AjusteInventarioRequest request) {
        validarAjusteRequest(request);

        Producto producto = productoRepository.findByIdForUpdate(request.idProducto())
                .orElseThrow(() -> new InventarioNotFoundException("Producto no encontrado: " + request.idProducto()));
        LoteProducto lote = obtenerLote(request.idLote(), producto);

        Integer stockAnterior = lote != null ? lote.getStockDisponible() : producto.getStockActual();
        Integer diferencia = request.stockFisico() - stockAnterior;

        if (diferencia == 0) {
            throw new InventarioBusinessException("El stock fisico es igual al stock registrado.");
        }

        if (lote != null) {
            lote.setStockDisponible(request.stockFisico());
            producto.setStockActual(producto.getStockActual() + diferencia);
            if (producto.getStockActual() < 0) {
                throw new InventarioBusinessException("El ajuste dejaria el producto con stock negativo.");
            }
        } else {
            producto.setStockActual(request.stockFisico());
        }

        MovimientoAlmacen movimiento = new MovimientoAlmacen();
        movimiento.setProducto(producto);
        movimiento.setLote(lote);
        movimiento.setIdTrabajador(request.idTrabajador());
        movimiento.setTipoMovimiento(TipoMovimiento.Ajuste);
        movimiento.setMotivo(MotivoMovimiento.Ajuste);
        movimiento.setCantidad(Math.abs(diferencia));
        movimiento.setReferenciaTipo(request.referenciaTipo());
        movimiento.setReferenciaId(request.referenciaId());
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setObservacion(observacionAjuste(request.observacion(), stockAnterior, request.stockFisico()));

        MovimientoAlmacen guardado = movimientoAlmacenRepository.save(movimiento);

        return toMovimientoAlmacenResponse(guardado);
    }

    private void validarRequest(MovimientoAlmacenRequest request) {
        if (request == null) {
            throw new InventarioBusinessException("El movimiento es obligatorio.");
        }
        if (request.idProducto() == null) {
            throw new InventarioBusinessException("El producto es obligatorio.");
        }
        if (request.idTrabajador() == null) {
            throw new InventarioBusinessException("El trabajador es obligatorio.");
        }
        if (request.cantidad() == null || request.cantidad() <= 0) {
            throw new InventarioBusinessException("La cantidad debe ser mayor que cero.");
        }
        if (request.tipoMovimiento() == null || request.tipoMovimiento().isBlank()) {
            throw new InventarioBusinessException("El tipo de movimiento es obligatorio.");
        }
        if (request.motivo() == null || request.motivo().isBlank()) {
            throw new InventarioBusinessException("El motivo es obligatorio.");
        }
    }

    private void validarAjusteRequest(AjusteInventarioRequest request) {
        if (request == null) {
            throw new InventarioBusinessException("El ajuste es obligatorio.");
        }
        if (request.idProducto() == null) {
            throw new InventarioBusinessException("El producto es obligatorio.");
        }
        if (request.idTrabajador() == null) {
            throw new InventarioBusinessException("El trabajador es obligatorio.");
        }
        if (request.stockFisico() == null || request.stockFisico() < 0) {
            throw new InventarioBusinessException("El stock fisico debe ser mayor o igual que cero.");
        }
    }

    private TipoMovimiento parseTipoMovimiento(String value) {
        try {
            return TipoMovimiento.valueOf(value.trim());
        } catch (IllegalArgumentException exception) {
            throw new InventarioBusinessException("Tipo de movimiento invalido: " + value);
        }
    }

    private MotivoMovimiento parseMotivo(String value) {
        try {
            return MotivoMovimiento.valueOf(value.trim());
        } catch (IllegalArgumentException exception) {
            throw new InventarioBusinessException("Motivo invalido: " + value);
        }
    }

    private LoteProducto obtenerLote(Integer idLote, Producto producto) {
        if (idLote == null) {
            return null;
        }

        LoteProducto lote = loteProductoRepository.findByIdForUpdate(idLote)
                .orElseThrow(() -> new InventarioNotFoundException("Lote no encontrado: " + idLote));

        if (!lote.getProducto().getIdProducto().equals(producto.getIdProducto())) {
            throw new InventarioBusinessException("El lote no pertenece al producto indicado.");
        }

        return lote;
    }

    private void sumarStock(Producto producto, LoteProducto lote, Integer cantidad) {
        producto.setStockActual(producto.getStockActual() + cantidad);

        if (lote != null) {
            lote.setStockDisponible(lote.getStockDisponible() + cantidad);
        }
    }

    private void restarStock(Producto producto, LoteProducto lote, Integer cantidad) {
        if (producto.getStockActual() < cantidad) {
            throw new InventarioBusinessException("Stock insuficiente para el producto.");
        }

        producto.setStockActual(producto.getStockActual() - cantidad);

        if (lote != null) {
            if (lote.getStockDisponible() < cantidad) {
                throw new InventarioBusinessException("Stock insuficiente para el lote.");
            }
            lote.setStockDisponible(lote.getStockDisponible() - cantidad);
        }
    }

    private String observacionAjuste(String observacion, Integer stockAnterior, Integer stockFisico) {
        String resumen = "Ajuste de inventario: stock anterior "
                + stockAnterior
                + ", stock fisico "
                + stockFisico
                + ".";

        if (observacion == null || observacion.isBlank()) {
            return resumen;
        }

        return resumen + " " + observacion.trim();
    }

    private MovimientoAlmacenResponse toMovimientoAlmacenResponse(MovimientoAlmacen movimiento) {
        Producto producto = movimiento.getProducto();
        LoteProducto lote = movimiento.getLote();

        return new MovimientoAlmacenResponse(
                movimiento.getIdMovimiento(),
                producto != null ? producto.getIdProducto() : null,
                producto != null ? producto.getNombre() : null,
                lote != null ? lote.getIdLote() : null,
                lote != null ? lote.getNumeroLote() : null,
                movimiento.getIdTrabajador(),
                movimiento.getTipoMovimiento() != null ? movimiento.getTipoMovimiento().name() : null,
                movimiento.getMotivo() != null ? movimiento.getMotivo().name() : null,
                movimiento.getCantidad(),
                movimiento.getReferenciaTipo(),
                movimiento.getReferenciaId(),
                movimiento.getFechaMovimiento(),
                movimiento.getObservacion()
        );
    }
}
