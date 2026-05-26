package com.farmape.backend.caja.model;

import com.farmape.backend.caja.enums.EstadoPagoVenta;
import com.farmape.backend.caja.enums.MetodoPago;
import com.farmape.backend.trabajadores.model.Trabajador;
import com.farmape.backend.ventas.model.OrdenVenta;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos_venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago_venta")
    private Integer idPagoVenta;

    @ManyToOne
    @JoinColumn(name = "id_orden_venta", nullable = false)
    private OrdenVenta ordenVenta;

    @ManyToOne
    @JoinColumn(name = "id_cajero", nullable = false)
    private Trabajador cajero;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "monto_pagado", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoPagado;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPagoVenta estado;
}