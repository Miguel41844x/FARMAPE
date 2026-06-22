package com.farmape.backend.almacen.model;

import com.farmape.backend.trabajadores.model.Trabajador;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "recepciones_compra")
@Getter
@Setter
public class RecepcionCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recepcion")
    private Integer idRecepcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orden_compra", nullable = false)
    private OrdenCompraAlmacen ordenCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_encargado_almacen", nullable = false)
    private Trabajador encargadoAlmacen;

    @Column(name = "fecha_recepcion")
    private LocalDateTime fechaRecepcion;

    @Column(name = "estado", length = 30)
    private String estado;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;
}
