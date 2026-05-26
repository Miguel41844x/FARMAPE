package com.farmape.backend.ventas.model;

import com.farmape.backend.clientes.model.Cliente;
import com.farmape.backend.trabajadores.model.Trabajador;
import com.farmape.backend.ventas.enums.CanalPedido;
import com.farmape.backend.ventas.enums.EstadoOrdenVenta;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordenes_venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden_venta")
    private Integer idOrdenVenta;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Trabajador empleado;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal_pedido", nullable = false)
    private CanalPedido canalPedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoOrdenVenta estado;

    @Column(name = "fecha_orden")
    private LocalDateTime fechaOrden;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;
}