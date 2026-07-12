package com.farmape.ms.inventario.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "despachos_operativos")
public class DespachoOperativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_despacho")
    private Integer idDespacho;

    @Column(name = "id_orden_venta", nullable = false)
    private Integer idOrdenVenta;

    @Column(name = "cliente", nullable = false, length = 180)
    private String cliente;

    @Column(name = "fecha_orden", nullable = false)
    private LocalDateTime fechaOrden;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "tipo_despacho", nullable = false, length = 20)
    private String tipoDespacho;

    @Column(name = "direccion", length = 180)
    private String direccion;

    @Column(name = "repartidor", length = 120)
    private String repartidor;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    public Integer getIdDespacho() {
        return idDespacho;
    }

    public void setIdDespacho(Integer idDespacho) {
        this.idDespacho = idDespacho;
    }

    public Integer getIdOrdenVenta() {
        return idOrdenVenta;
    }

    public void setIdOrdenVenta(Integer idOrdenVenta) {
        this.idOrdenVenta = idOrdenVenta;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getFechaOrden() {
        return fechaOrden;
    }

    public void setFechaOrden(LocalDateTime fechaOrden) {
        this.fechaOrden = fechaOrden;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getTipoDespacho() {
        return tipoDespacho;
    }

    public void setTipoDespacho(String tipoDespacho) {
        this.tipoDespacho = tipoDespacho;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(String repartidor) {
        this.repartidor = repartidor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }
}
