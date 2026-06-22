package com.farmape.backend.despacho.model;

import com.farmape.backend.trabajadores.model.Trabajador;
import com.farmape.backend.ventas.model.OrdenVenta;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "despachos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Despacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_despacho")
    private Integer idDespacho;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orden_venta", nullable = false)
    private OrdenVenta ordenVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_encargado_despacho", nullable = false)
    private Trabajador encargadoDespacho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_repartidor")
    private Trabajador repartidor;

    @Column(name = "tipo_despacho", nullable = false, length = 20)
    private String tipoDespacho;

    @Column(name = "direccion_entrega", length = 180)
    private String direccionEntrega;

    @Column(name = "fecha_despacho")
    private LocalDateTime fechaDespacho;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @Column(name = "comprobante_visado")
    private Boolean comprobanteVisado;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;
}
