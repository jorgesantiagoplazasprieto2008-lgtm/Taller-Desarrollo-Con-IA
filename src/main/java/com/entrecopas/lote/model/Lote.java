package com.entrecopas.lote.model;

import com.entrecopas.producto.model.Producto;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad Lote que almacena los datos productivos, analíticos y de trazabilidad.
 */
@Entity
@Table(name = "lote")
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "codigo_trazabilidad", nullable = false, unique = true, length = 35)
    private String codigoTrazabilidad;

    @Column(name = "fecha_produccion", nullable = false)
    private LocalDate fechaProduccion;

    @Column(name = "volumen_litros", nullable = false, precision = 10, scale = 2)
    private BigDecimal volumenLitros;

    @Column(name = "merma_litros", nullable = false, precision = 10, scale = 2)
    private BigDecimal mermaLitros = BigDecimal.ZERO;

    @Column(name = "costo_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoTotal;

    @Convert(converter = ParametrosAnaliticosConverter.class)
    @Column(name = "parametros_analiticos", nullable = false, columnDefinition = "JSON")
    private ParametrosAnaliticos parametrosAnaliticos = new ParametrosAnaliticos();

    @Column(name = "estado", nullable = false, length = 25)
    private String estado = "LIBERADO";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Lote() {
    }

    public Lote(Long id, Producto producto, String codigoTrazabilidad, LocalDate fechaProduccion,
                BigDecimal volumenLitros, BigDecimal mermaLitros, BigDecimal costoTotal,
                ParametrosAnaliticos parametrosAnaliticos) {
        this(id, producto, codigoTrazabilidad, fechaProduccion, volumenLitros, mermaLitros, costoTotal,
                parametrosAnaliticos, "LIBERADO");
    }

    public Lote(Long id, Producto producto, String codigoTrazabilidad, LocalDate fechaProduccion,
                BigDecimal volumenLitros, BigDecimal mermaLitros, BigDecimal costoTotal,
                ParametrosAnaliticos parametrosAnaliticos, String estado) {
        this.id = id;
        this.producto = producto;
        this.codigoTrazabilidad = codigoTrazabilidad;
        this.fechaProduccion = fechaProduccion;
        this.volumenLitros = volumenLitros;
        this.mermaLitros = mermaLitros != null ? mermaLitros : BigDecimal.ZERO;
        this.costoTotal = costoTotal;
        this.parametrosAnaliticos = parametrosAnaliticos != null ? parametrosAnaliticos : new ParametrosAnaliticos();
        this.estado = estado != null && !estado.trim().isEmpty() ? estado.trim().toUpperCase() : "LIBERADO";
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.mermaLitros == null) {
            this.mermaLitros = BigDecimal.ZERO;
        }
        if (this.parametrosAnaliticos == null) {
            this.parametrosAnaliticos = new ParametrosAnaliticos();
        }
        if (this.estado == null || this.estado.trim().isEmpty()) {
            this.estado = "LIBERADO";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getCodigoTrazabilidad() {
        return codigoTrazabilidad;
    }

    public void setCodigoTrazabilidad(String codigoTrazabilidad) {
        this.codigoTrazabilidad = codigoTrazabilidad;
    }

    public LocalDate getFechaProduccion() {
        return fechaProduccion;
    }

    public void setFechaProduccion(LocalDate fechaProduccion) {
        this.fechaProduccion = fechaProduccion;
    }

    public BigDecimal getVolumenLitros() {
        return volumenLitros;
    }

    public void setVolumenLitros(BigDecimal volumenLitros) {
        this.volumenLitros = volumenLitros;
    }

    public BigDecimal getMermaLitros() {
        return mermaLitros;
    }

    public void setMermaLitros(BigDecimal mermaLitros) {
        this.mermaLitros = mermaLitros;
    }

    public BigDecimal getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(BigDecimal costoTotal) {
        this.costoTotal = costoTotal;
    }

    public ParametrosAnaliticos getParametrosAnaliticos() {
        return parametrosAnaliticos;
    }

    public void setParametrosAnaliticos(ParametrosAnaliticos parametrosAnaliticos) {
        this.parametrosAnaliticos = parametrosAnaliticos;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
