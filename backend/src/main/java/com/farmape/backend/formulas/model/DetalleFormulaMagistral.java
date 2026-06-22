package com.farmape.backend.formulas.model;

import com.farmape.backend.productos.model.Producto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_formula_magistral")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleFormulaMagistral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_formula")
    private Integer idDetalleFormula;

    @ManyToOne
    @JoinColumn(name = "id_formula", nullable = false)
    private FormulaMagistral formula;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(name = "id_lote")
    private Integer idLote;

    @Column(name = "cantidad_usada", nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidadUsada;

    @Column(name = "unidad_medida", nullable = false, length = 30)
    private String unidadMedida;
}
