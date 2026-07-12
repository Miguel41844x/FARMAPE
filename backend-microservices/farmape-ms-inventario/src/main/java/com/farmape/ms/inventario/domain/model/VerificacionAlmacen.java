package com.farmape.ms.inventario.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "verificaciones_almacen")
public class VerificacionAlmacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_verificacion")
    private Integer idVerificacion;

    @Column(name = "id_pedido_compra")
    private Integer idPedidoCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(name = "cantidad_pedida", nullable = false)
    private Integer cantidadPedida;

    @Column(name = "cantidad_recibida", nullable = false)
    private Integer cantidadRecibida;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "CONFORME";

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    @Column(name = "fecha_verificacion", nullable = false)
    private LocalDateTime fechaVerificacion;

    public Integer getIdVerificacion() {
        return idVerificacion;
    }

    public void setIdVerificacion(Integer idVerificacion) {
        this.idVerificacion = idVerificacion;
    }

    public Integer getIdPedidoCompra() {
        return idPedidoCompra;
    }

    public void setIdPedidoCompra(Integer idPedidoCompra) {
        this.idPedidoCompra = idPedidoCompra;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidadPedida() {
        return cantidadPedida;
    }

    public void setCantidadPedida(Integer cantidadPedida) {
        this.cantidadPedida = cantidadPedida;
    }

    public Integer getCantidadRecibida() {
        return cantidadRecibida;
    }

    public void setCantidadRecibida(Integer cantidadRecibida) {
        this.cantidadRecibida = cantidadRecibida;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public LocalDateTime getFechaVerificacion() {
        return fechaVerificacion;
    }

    public void setFechaVerificacion(LocalDateTime fechaVerificacion) {
        this.fechaVerificacion = fechaVerificacion;
    }
}
