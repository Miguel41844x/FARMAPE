package com.farmape.backend.compras.model;

import com.farmape.backend.trabajadores.model.Trabajador;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordenes_compra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden_compra")
    private Integer idOrdenCompra;

    @Column(name = "numero_orden", unique = true, length = 30)
    private String numeroOrden;

    @ManyToOne
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;

    @ManyToOne
    @JoinColumn(name = "id_administrador", nullable = false)
    private Trabajador administrador;

    @Column(name = "fecha_orden")
    private LocalDateTime fechaOrden;

    @Column(name = "fecha_entrega")
    private LocalDate fechaEntrega;

    @Column(name = "medio_pedido", nullable = false, length = 30)
    private String medioPedido;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleOrdenCompra> detalles = new ArrayList<>();

    public void agregarDetalle(DetalleOrdenCompra detalle) {
        detalles.add(detalle);
        detalle.setOrdenCompra(this);
    }
}
