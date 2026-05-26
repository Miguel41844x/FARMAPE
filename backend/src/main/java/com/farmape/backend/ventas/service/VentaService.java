package com.farmape.backend.ventas.service;

import com.farmape.backend.clientes.model.Cliente;
import com.farmape.backend.clientes.repository.ClienteRepository;
import com.farmape.backend.productos.enums.EstadoProducto;
import com.farmape.backend.productos.model.Producto;
import com.farmape.backend.productos.repository.ProductoRepository;
import com.farmape.backend.trabajadores.model.Trabajador;
import com.farmape.backend.trabajadores.repository.TrabajadorRepository;
import com.farmape.backend.ventas.dto.*;
import com.farmape.backend.ventas.enums.EstadoOrdenVenta;
import com.farmape.backend.ventas.model.DetalleOrdenVenta;
import com.farmape.backend.ventas.model.OrdenVenta;
import com.farmape.backend.ventas.repository.DetalleOrdenVentaRepository;
import com.farmape.backend.ventas.repository.OrdenVentaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VentaService {

    private final OrdenVentaRepository ordenVentaRepository;
    private final DetalleOrdenVentaRepository detalleOrdenVentaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final TrabajadorRepository trabajadorRepository;

    public VentaService(
            OrdenVentaRepository ordenVentaRepository,
            DetalleOrdenVentaRepository detalleOrdenVentaRepository,
            ClienteRepository clienteRepository,
            ProductoRepository productoRepository,
            TrabajadorRepository trabajadorRepository
    ) {
        this.ordenVentaRepository = ordenVentaRepository;
        this.detalleOrdenVentaRepository = detalleOrdenVentaRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.trabajadorRepository = trabajadorRepository;
    }

    @Transactional
    public OrdenVentaResponse crearOrden(CrearOrdenVentaRequest request) {
        Cliente cliente = clienteRepository.findById(request.idCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Trabajador empleado = trabajadorRepository.findById(request.idEmpleado())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        OrdenVenta orden = OrdenVenta.builder()
                .cliente(cliente)
                .empleado(empleado)
                .canalPedido(request.canalPedido())
                .estado(EstadoOrdenVenta.Pendiente)
                .fechaOrden(LocalDateTime.now())
                .total(BigDecimal.ZERO)
                .observacion(request.observacion())
                .build();

        orden = ordenVentaRepository.save(orden);

        BigDecimal total = BigDecimal.ZERO;

        for (DetalleVentaRequest detalleRequest : request.detalles()) {
            Producto producto = productoRepository.findById(detalleRequest.idProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (producto.getEstado() != EstadoProducto.Activo) {
                throw new RuntimeException("El producto " + producto.getNombre() + " no está activo");
            }

            if (producto.getStockActual() < detalleRequest.cantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            BigDecimal precioUnitario = producto.getPrecioVenta();
            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(detalleRequest.cantidad()));

            DetalleOrdenVenta detalle = DetalleOrdenVenta.builder()
                    .ordenVenta(orden)
                    .producto(producto)
                    .cantidad(detalleRequest.cantidad())
                    .precioUnitario(precioUnitario)
                    .subtotal(subtotal)
                    .build();

            detalleOrdenVentaRepository.save(detalle);

            total = total.add(subtotal);
        }

        orden.setTotal(total);
        orden = ordenVentaRepository.save(orden);

        return toResponse(orden);
    }

    public List<OrdenVentaResponse> listar() {
        return ordenVentaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<OrdenVentaResponse> listarPendientes() {
        return ordenVentaRepository.findByEstado(EstadoOrdenVenta.Pendiente)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public OrdenVentaResponse obtenerPorId(Integer id) {
        OrdenVenta orden = ordenVentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de venta no encontrada"));

        return toResponse(orden);
    }

    @Transactional
    public OrdenVentaResponse anular(Integer id) {
        OrdenVenta orden = ordenVentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de venta no encontrada"));

        if (orden.getEstado() != EstadoOrdenVenta.Pendiente) {
            throw new RuntimeException("Solo se pueden anular órdenes pendientes");
        }

        orden.setEstado(EstadoOrdenVenta.Anulada);

        return toResponse(ordenVentaRepository.save(orden));
    }

    private OrdenVentaResponse toResponse(OrdenVenta orden) {
        List<DetalleVentaResponse> detalles = detalleOrdenVentaRepository.findByOrdenVenta(orden)
                .stream()
                .map(detalle -> new DetalleVentaResponse(
                        detalle.getIdDetalleVenta(),
                        detalle.getProducto().getIdProducto(),
                        detalle.getProducto().getNombre(),
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario(),
                        detalle.getSubtotal()
                ))
                .toList();

        return new OrdenVentaResponse(
                orden.getIdOrdenVenta(),
                orden.getCliente().getIdCliente(),
                orden.getCliente().getNombres() + " " +
                        (orden.getCliente().getApellidos() != null ? orden.getCliente().getApellidos() : ""),
                orden.getEmpleado().getIdTrabajador(),
                orden.getEmpleado().getNombres() + " " + orden.getEmpleado().getApellidos(),
                orden.getCanalPedido(),
                orden.getEstado(),
                orden.getFechaOrden(),
                orden.getTotal(),
                orden.getObservacion(),
                detalles
        );
    }
}