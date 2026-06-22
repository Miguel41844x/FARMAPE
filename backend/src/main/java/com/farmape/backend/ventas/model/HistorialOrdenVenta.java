package com.farmape.backend.ventas.model;

import com.farmape.backend.trabajadores.model.Trabajador;
import com.farmape.backend.ventas.enums.EstadoOrdenVenta;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_orden_venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialOrdenVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Long idHistorial;

    @ManyToOne
    @JoinColumn(name = "id_orden_venta", nullable = false)
    private OrdenVenta ordenVenta;

    @ManyToOne
    @JoinColumn(name = "id_trabajador", nullable = false)
    private Trabajador trabajador;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", length = 30)
    private EstadoOrdenVenta estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false, length = 30)
    private EstadoOrdenVenta estadoNuevo;

    @Column(name = "observacion", length = 255)
    private String observacion;

    @Column(name = "fecha_cambio")
    private LocalDateTime fechaCambio;
}
