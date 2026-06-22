package com.farmape.backend.compras.model;

import com.farmape.backend.trabajadores.model.Trabajador;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos_proveedor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago_proveedor")
    private Integer idPagoProveedor;

    @ManyToOne
    @JoinColumn(name = "id_factura_compra", nullable = false)
    private FacturaCompra facturaCompra;

    @ManyToOne
    @JoinColumn(name = "id_administrador", nullable = false)
    private Trabajador administrador;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Column(name = "monto_pagado", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoPagado;

    @Column(name = "metodo_pago", nullable = false, length = 30)
    private String metodoPago;

    @Column(name = "referencia_operacion", length = 100)
    private String referenciaOperacion;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;
}
