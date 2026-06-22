package com.farmape.backend.caja.model;

import com.farmape.backend.caja.enums.TipoComprobante;
import com.farmape.backend.ventas.model.OrdenVenta;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "comprobantes_venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprobanteVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comprobante")
    private Integer idComprobante;

    @ManyToOne
    @JoinColumn(name = "id_orden_venta", nullable = false)
    private OrdenVenta ordenVenta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", nullable = false)
    private TipoComprobante tipoComprobante;

    @Column(name = "serie", length = 10)
    private String serie;

    @Column(name = "numero", length = 20)
    private String numero;

    @Column(name = "fecha_emision")
    private LocalDateTime fechaEmision;

    @Column(name = "monto_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoTotal;
}
