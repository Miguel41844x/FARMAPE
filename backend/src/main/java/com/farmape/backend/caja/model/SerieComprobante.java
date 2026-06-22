package com.farmape.backend.caja.model;

import com.farmape.backend.caja.enums.TipoComprobante;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "series_comprobante")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SerieComprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_serie")
    private Integer idSerie;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", nullable = false, length = 20)
    private TipoComprobante tipoComprobante;

    @Column(name = "serie", nullable = false, length = 10)
    private String serie;

    @Column(name = "ultimo_numero", nullable = false)
    private Long ultimoNumero;

    @Column(name = "activo", nullable = false)
    private Boolean activo;
}
