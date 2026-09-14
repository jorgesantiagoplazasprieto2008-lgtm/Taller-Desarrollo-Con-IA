package com.entrecopas.disponibilidad.model;

import com.entrecopas.establecimiento.model.Establecimiento;
import com.entrecopas.producto.model.Producto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

/**
 * Entidad de disponibilidad de un producto artesanal en un establecimiento de hostelería.
 */
@Entity
@Table(
    name = "disponibilidad",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_prod_est",
        columnNames = {"producto_id", "establecimiento_id"}
    )
)
public class Disponibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "establecimiento_id", nullable = false)
    private Establecimiento establecimiento;

    @Column(name = "disponible", nullable = false)
    private Boolean disponible = true;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Disponibilidad() {
    }

    public Disponibilidad(Long id, Producto producto, Establecimiento establecimiento, Boolean disponible) {
        this.id = id;
        this.producto = producto;
        this.establecimiento = establecimiento;
        this.disponible = disponible != null ? disponible : true;
    }

    @PrePersist
    @PreUpdate
    protected void onPersist() {
        this.updatedAt = LocalDateTime.now();
        if (this.disponible == null) {
            this.disponible = true;
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

    public Establecimiento getEstablecimiento() {
        return establecimiento;
    }

    public void setEstablecimiento(Establecimiento establecimiento) {
        this.establecimiento = establecimiento;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
