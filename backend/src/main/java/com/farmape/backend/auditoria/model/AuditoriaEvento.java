package com.farmape.backend.auditoria.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_eventos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Long idAuditoria;

    @Column(name = "fecha_evento", nullable = false)
    private LocalDateTime fechaEvento;

    @Column(name = "modulo", nullable = false, length = 50)
    private String modulo;

    @Column(name = "entidad", nullable = false, length = 80)
    private String entidad;

    @Column(name = "entidad_id", length = 80)
    private String entidadId;

    @Column(name = "accion", nullable = false, length = 40)
    private String accion;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "valor_anterior", columnDefinition = "TEXT")
    private String valorAnterior;

    @Column(name = "valor_nuevo", columnDefinition = "TEXT")
    private String valorNuevo;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "id_trabajador")
    private Integer idTrabajador;

    @Column(name = "usuario", length = 80)
    private String usuario;

    @Column(name = "severidad", nullable = false, length = 20)
    private String severidad;

    @Column(name = "origen", nullable = false, length = 30)
    private String origen;

    @Column(name = "ip", length = 45)
    private String ip;

    @PrePersist
    public void prePersist() {
        if (fechaEvento == null) {
            fechaEvento = LocalDateTime.now();
        }
        if (severidad == null || severidad.isBlank()) {
            severidad = "INFO";
        }
        if (origen == null || origen.isBlank()) {
            origen = "APP";
        }
    }
}
