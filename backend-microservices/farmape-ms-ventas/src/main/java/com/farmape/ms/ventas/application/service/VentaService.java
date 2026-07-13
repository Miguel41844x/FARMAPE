package com.farmape.ms.ventas.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.farmape.ms.ventas.api.dto.ActualizarVentaRequest;
import com.farmape.ms.ventas.api.dto.CrearVentaRequest;
import com.farmape.ms.ventas.api.dto.DetalleVentaRequest;
import com.farmape.ms.ventas.api.dto.DetalleVentaResponse;
import com.farmape.ms.ventas.api.dto.VentaResponse;
import com.farmape.ms.ventas.application.client.InventarioClient;
import com.farmape.ms.ventas.application.client.InventarioProductoResponse;
import com.farmape.ms.ventas.application.exception.VentaBusinessException;
import com.farmape.ms.ventas.application.exception.VentaNotFoundException;
import com.farmape.ms.ventas.domain.model.CanalPedido;
import com.farmape.ms.ventas.domain.model.DetalleVenta;
import com.farmape.ms.ventas.domain.model.EstadoVenta;
import com.farmape.ms.ventas.domain.model.Venta;
import com.farmape.ms.ventas.domain.repository.VentaRepository;

@Service
public class VentaService {

    private static final String ESTADO_PRODUCTO_ACTIVO = "Activo";

    private final VentaRepository ventaRepository;
    private final InventarioClient inventarioClient;

    public VentaService(VentaRepository ventaRepository, InventarioClient inventarioClient) {
        this.ventaRepository = ventaRepository;
        this.inventarioClient = inventarioClient;
    }

    public VentaResponse registrarVenta(CrearVentaRequest request) {
        if (request == null) {
            throw new VentaBusinessException("La venta es obligatoria.");
        }
        validarCabecera(request.idCliente(), request.canalPedido());

        List<DetalleVentaRequest> items = consolidarProductos(detallesRequest(request.detalles(), request.productos()));
        List<DetalleVenta> detalles = construirDetalles(items);
        BigDecimal total = calcularTotal(detalles);
        Integer idOrdenVenta = siguienteIdOrdenVenta();

        List<DetalleVenta> stockReducido = new ArrayList<>();
        try {
            reducirStock(detalles, idOrdenVenta, stockReducido);

            LocalDateTime ahora = LocalDateTime.now();
            Venta venta = new Venta();
            venta.setIdOrdenVenta(idOrdenVenta);
            venta.setIdCliente(request.idCliente());
            venta.setCliente(clienteSeguro(request.cliente(), request.nombreCliente(), request.idCliente()));
            venta.setIdEmpleado(empleadoIdSeguro(request.idEmpleado()));
            venta.setEmpleado(empleadoSeguro(request.empleado(), request.idEmpleado()));
            venta.setCanalPedido(request.canalPedido());
            venta.setEstado(EstadoVenta.Pendiente);
            venta.setFechaOrden(ahora);
            venta.setTotal(total);
            venta.setObservacion(request.observacion());
            venta.setDetalles(detalles);

            return toVentaResponse(ventaRepository.save(venta));
        } catch (RuntimeException exception) {
            compensarStock(stockReducido, idOrdenVenta, exception);
            throw exception;
        }
    }

    public List<VentaResponse> listarVentas() {
        return ventaRepository.findAllByOrderByFechaOrdenDesc()
                .stream()
                .map(this::toVentaResponse)
                .toList();
    }

    public VentaResponse obtenerVenta(Integer idVenta) {
        return toVentaResponse(obtenerVentaEntidad(idVenta));
    }

    public VentaResponse obtenerDetalleVenta(Integer idVenta) {
        return obtenerVenta(idVenta);
    }

    public List<VentaResponse> listarVentasPorCliente(Integer idCliente) {
        if (idCliente == null) {
            throw new VentaBusinessException("El cliente es obligatorio.");
        }
        return ventaRepository.findByIdClienteOrderByFechaOrdenDesc(idCliente)
                .stream()
                .map(this::toVentaResponse)
                .toList();
    }

    public List<VentaResponse> listarVentasPorEstado(String estado) {
        return ventaRepository.findByEstadoOrderByFechaOrdenDesc(parseEstado(estado))
                .stream()
                .map(this::toVentaResponse)
                .toList();
    }

    public VentaResponse actualizarVenta(Integer idVenta, ActualizarVentaRequest request) {
        if (request == null) {
            throw new VentaBusinessException("La venta es obligatoria.");
        }
        Venta venta = obtenerVentaEntidad(idVenta);
        validarVentaPendiente(venta, "Solo se pueden actualizar ventas pendientes.");
        validarCabecera(request.idCliente(), request.canalPedido());

        List<DetalleVentaRequest> items = consolidarProductos(detallesRequest(request.detalles(), request.productos()));
        List<DetalleVenta> nuevosDetalles = construirDetalles(items);
        BigDecimal total = calcularTotal(nuevosDetalles);

        List<DetalleVenta> stockRestaurado = new ArrayList<>();
        List<DetalleVenta> stockReducido = new ArrayList<>();
        try {
            restaurarStock(venta.getDetalles(), venta.getIdOrdenVenta(), stockRestaurado);
            reducirStock(nuevosDetalles, venta.getIdOrdenVenta(), stockReducido);

            venta.setIdCliente(request.idCliente());
            venta.setCliente(clienteSeguro(request.cliente(), request.nombreCliente(), request.idCliente()));
            venta.setIdEmpleado(empleadoIdSeguro(request.idEmpleado()));
            venta.setEmpleado(empleadoSeguro(request.empleado(), request.idEmpleado()));
            venta.setCanalPedido(request.canalPedido());
            venta.setObservacion(request.observacion());
            venta.setDetalles(nuevosDetalles);
            venta.setTotal(total);

            return toVentaResponse(ventaRepository.save(venta));
        } catch (RuntimeException exception) {
            compensarStock(stockReducido, venta.getIdOrdenVenta(), exception);
            reducirStockOriginalDespuesDeActualizacionFallida(stockRestaurado, venta.getIdOrdenVenta(), exception);
            throw exception;
        }
    }

    public VentaResponse completarVenta(Integer idVenta) {
        Venta venta = obtenerVentaEntidad(idVenta);
        validarVentaPendiente(venta, "Solo se pueden confirmar ordenes pendientes.");

        venta.setEstado(EstadoVenta.Confirmada);
        return toVentaResponse(ventaRepository.save(venta));
    }

    public VentaResponse cancelarVenta(Integer idVenta) {
        Venta venta = obtenerVentaEntidad(idVenta);
        validarVentaPendiente(venta, "Solo se pueden cancelar ventas pendientes.");

        List<DetalleVenta> stockRestaurado = new ArrayList<>();
        try {
            restaurarStock(venta.getDetalles(), venta.getIdOrdenVenta(), stockRestaurado);

            venta.setEstado(EstadoVenta.Anulada);
            return toVentaResponse(ventaRepository.save(venta));
        } catch (RuntimeException exception) {
            reducirStockOriginalDespuesDeActualizacionFallida(stockRestaurado, venta.getIdOrdenVenta(), exception);
            throw exception;
        }
    }

    private void validarCabecera(Integer idCliente, CanalPedido canalPedido) {
        if (idCliente == null) {
            throw new VentaBusinessException("El cliente es obligatorio.");
        }
        if (canalPedido == null) {
            throw new VentaBusinessException("El canal de pedido es obligatorio.");
        }
    }

    private List<DetalleVentaRequest> consolidarProductos(List<DetalleVentaRequest> productos) {
        if (productos == null || productos.isEmpty()) {
            throw new VentaBusinessException("La venta debe tener al menos un producto.");
        }

        Map<Integer, Integer> cantidadesPorProducto = new LinkedHashMap<>();
        for (DetalleVentaRequest producto : productos) {
            if (producto == null || producto.idProducto() == null) {
                throw new VentaBusinessException("El producto es obligatorio.");
            }
            if (producto.cantidad() == null || producto.cantidad() <= 0) {
                throw new VentaBusinessException("La cantidad debe ser mayor que cero.");
            }
            cantidadesPorProducto.merge(producto.idProducto(), producto.cantidad(), Integer::sum);
        }

        return cantidadesPorProducto.entrySet()
                .stream()
                .map(entry -> new DetalleVentaRequest(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<DetalleVenta> construirDetalles(List<DetalleVentaRequest> productos) {
        List<DetalleVenta> detalles = new ArrayList<>();
        int idDetalle = 1;
        for (DetalleVentaRequest item : productos) {
            InventarioProductoResponse producto = inventarioClient.obtenerProducto(item.idProducto());
            validarProductoDisponible(producto, item.cantidad());

            DetalleVenta detalle = new DetalleVenta();
            detalle.setIdDetalleVenta(idDetalle++);
            detalle.setIdProducto(producto.idProducto());
            detalle.setProducto(producto.nombre());
            detalle.setCantidad(item.cantidad());
            detalle.setPrecioUnitario(producto.precioVenta());
            detalle.setSubtotal(producto.precioVenta().multiply(BigDecimal.valueOf(item.cantidad())));
            detalles.add(detalle);
        }
        return detalles;
    }

    private void validarProductoDisponible(InventarioProductoResponse producto, Integer cantidad) {
        if (producto == null || producto.idProducto() == null) {
            throw new VentaBusinessException("Producto no encontrado.");
        }
        if (!ESTADO_PRODUCTO_ACTIVO.equals(producto.estado())) {
            throw new VentaBusinessException("El producto no esta activo: " + producto.idProducto());
        }
        if (producto.precioVenta() == null || producto.precioVenta().compareTo(BigDecimal.ZERO) <= 0) {
            throw new VentaBusinessException("El producto no tiene un precio de venta valido: " + producto.idProducto());
        }
        if (producto.stockActual() == null || producto.stockActual() < cantidad) {
            throw new VentaBusinessException("Stock insuficiente para el producto: " + producto.idProducto());
        }
    }

    private BigDecimal calcularTotal(List<DetalleVenta> detalles) {
        return detalles.stream()
                .map(DetalleVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void reducirStock(List<DetalleVenta> detalles, Integer idVenta, List<DetalleVenta> stockReducido) {
        for (DetalleVenta detalle : detalles) {
            inventarioClient.reducirStock(detalle.getIdProducto(), detalle.getCantidad(), idVenta);
            stockReducido.add(detalle);
        }
    }

    private void restaurarStock(List<DetalleVenta> detalles, Integer idVenta, List<DetalleVenta> stockRestaurado) {
        for (DetalleVenta detalle : detalles) {
            inventarioClient.restaurarStock(detalle.getIdProducto(), detalle.getCantidad(), idVenta);
            stockRestaurado.add(detalle);
        }
    }

    private void compensarStock(List<DetalleVenta> stockReducido, Integer idVenta, RuntimeException original) {
        for (DetalleVenta detalle : stockReducido.reversed()) {
            try {
                inventarioClient.restaurarStock(detalle.getIdProducto(), detalle.getCantidad(), idVenta);
            } catch (RuntimeException compensacion) {
                original.addSuppressed(compensacion);
            }
        }
    }

    private void reducirStockOriginalDespuesDeActualizacionFallida(
            List<DetalleVenta> stockRestaurado,
            Integer idVenta,
            RuntimeException original
    ) {
        for (DetalleVenta detalle : stockRestaurado) {
            try {
                inventarioClient.reducirStock(detalle.getIdProducto(), detalle.getCantidad(), idVenta);
            } catch (RuntimeException compensacion) {
                original.addSuppressed(compensacion);
            }
        }
    }

    private Venta obtenerVentaEntidad(Integer idVenta) {
        if (idVenta == null) {
            throw new VentaBusinessException("La venta es obligatoria.");
        }
        return ventaRepository.findByIdOrdenVenta(idVenta)
                .orElseThrow(() -> new VentaNotFoundException("Venta no encontrada: " + idVenta));
    }

    private void validarVentaPendiente(Venta venta, String mensaje) {
        if (venta.getEstado() == EstadoVenta.Anulada || venta.getEstado() == EstadoVenta.Rechazada) {
            throw new VentaBusinessException("La orden anulada o rechazada no puede modificarse.");
        }
        if (venta.getEstado() != EstadoVenta.Pendiente) {
            throw new VentaBusinessException(mensaje);
        }
    }

    private EstadoVenta parseEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new VentaBusinessException("El estado es obligatorio.");
        }
        try {
            return EstadoVenta.valueOf(estado.trim());
        } catch (IllegalArgumentException exception) {
            throw new VentaBusinessException("Estado de venta invalido: " + estado);
        }
    }

    private VentaResponse toVentaResponse(Venta venta) {
        return new VentaResponse(
                venta.getIdOrdenVenta(),
                venta.getIdCliente(),
                venta.getCliente(),
                venta.getIdEmpleado(),
                venta.getEmpleado(),
                venta.getCanalPedido(),
                venta.getEstado(),
                venta.getFechaOrden(),
                venta.getTotal(),
                venta.getObservacion(),
                venta.getDetalles().stream().map(this::toDetalleVentaResponse).toList()
        );
    }

    private DetalleVentaResponse toDetalleVentaResponse(DetalleVenta detalle) {
        return new DetalleVentaResponse(
                detalle.getIdDetalleVenta(),
                detalle.getIdProducto(),
                detalle.getProducto(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getSubtotal()
        );
    }

    private List<DetalleVentaRequest> detallesRequest(
            List<DetalleVentaRequest> detalles,
            List<DetalleVentaRequest> productos
    ) {
        return detalles != null ? detalles : productos;
    }

    private String clienteSeguro(String cliente, String nombreCliente, Integer idCliente) {
        if (cliente != null && !cliente.isBlank()) {
            return cliente.trim();
        }
        if (nombreCliente != null && !nombreCliente.isBlank()) {
            return nombreCliente.trim();
        }
        return "Cliente " + idCliente;
    }

    private Integer empleadoIdSeguro(Integer idEmpleado) {
        return idEmpleado != null ? idEmpleado : 1;
    }

    private String empleadoSeguro(String empleado, Integer idEmpleado) {
        if (empleado != null && !empleado.isBlank()) {
            return empleado.trim();
        }
        return "Empleado " + empleadoIdSeguro(idEmpleado);
    }

    private synchronized Integer siguienteIdOrdenVenta() {
        var ultimaVenta = ventaRepository.findTopByOrderByIdOrdenVentaDesc();
        if (ultimaVenta == null) {
            return 1;
        }
        return ultimaVenta
                .map(Venta::getIdOrdenVenta)
                .map(id -> id + 1)
                .orElse(1);
    }
}
