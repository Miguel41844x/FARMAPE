package com.farmape.ms.auth.auth.model;

import com.farmape.ms.auth.usuarios.model.CuentaUsuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_restablecimiento_clave")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudRestablecimientoClave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long idSolicitud;

    @Column(name = "usuario_o_correo", nullable = false, length = 100)
    private String usuarioOCorreo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cuenta")
    private CuentaUsuario cuentaUsuario;

    @Column(name = "mensaje", length = 300)
    private String mensaje;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @PrePersist
    public void prePersist() {
        if (fechaSolicitud == null) {
            fechaSolicitud = LocalDateTime.now();
        }
        if (estado == null || estado.isBlank()) {
            estado = "Pendiente";
        }
    }
}
