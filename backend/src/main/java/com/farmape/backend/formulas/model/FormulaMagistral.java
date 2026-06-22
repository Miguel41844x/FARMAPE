package com.farmape.backend.formulas.model;

import com.farmape.backend.ventas.model.OrdenVenta;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "formulas_magistrales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormulaMagistral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_formula")
    private Integer idFormula;

    @OneToOne
    @JoinColumn(name = "id_receta", nullable = false)
    private RecetaMagistral receta;

    @ManyToOne
    @JoinColumn(name = "id_orden_venta")
    private OrdenVenta ordenVenta;

    @Column(name = "fecha_elaboracion")
    private LocalDateTime fechaElaboracion;

    @Column(name = "descripcion_formula", nullable = false, columnDefinition = "TEXT")
    private String descripcionFormula;

    @Column(name = "instrucciones_uso", columnDefinition = "TEXT")
    private String instruccionesUso;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;
}
