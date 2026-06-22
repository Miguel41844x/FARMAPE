package com.farmape.backend.formulas.model;

import com.farmape.backend.clientes.model.Cliente;
import com.farmape.backend.trabajadores.model.Trabajador;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recetas_magistrales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecetaMagistral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_receta")
    private Integer idReceta;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_quimico_farmaceutico", nullable = false)
    private Trabajador quimicoFarmaceutico;

    @Column(name = "medico_prescriptor", length = 150)
    private String medicoPrescriptor;

    @Column(name = "numero_colegiatura", length = 30)
    private String numeroColegiatura;

    @Column(name = "descripcion_receta", nullable = false, columnDefinition = "TEXT")
    private String descripcionReceta;

    @Column(name = "contraindicaciones", columnDefinition = "TEXT")
    private String contraindicaciones;

    @Column(name = "presupuesto", precision = 12, scale = 2)
    private BigDecimal presupuesto;

    @Column(name = "fecha_receta")
    private LocalDateTime fechaReceta;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;
}
