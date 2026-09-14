package com.entrecopas.disponibilidad.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO que representa un producto artesanal en el panel de control de hostelería
 * con el estado actual de disponibilidad en barra.
 */
public class DisponibilidadProductoDTO {

    private Long productoId;
    private String productoNombre;
    private String tipoBebida;
    private String presentacion;
    private String productorNombre;
    private BigDecimal precioSugerido;
    private boolean disponible;
    private LocalDateTime updatedAt;

    public DisponibilidadProductoDTO() {
    }

    public DisponibilidadProductoDTO(Long productoId,
                                     String productoNombre,
                                     String tipoBebida,
                                     String presentacion,
                                     String productorNombre,
                                     BigDecimal precioSugerido,
                                     boolean disponible,
                                     LocalDateTime updatedAt) {
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.tipoBebida = tipoBebida;
        this.presentacion = presentacion;
        this.productorNombre = productorNombre;
        this.precioSugerido = precioSugerido;
        this.disponible = disponible;
        this.updatedAt = updatedAt;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public String getTipoBebida() {
        return tipoBebida;
    }

    public void setTipoBebida(String tipoBebida) {
        this.tipoBebida = tipoBebida;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public String getProductorNombre() {
        return productorNombre;
    }

    public void setProductorNombre(String productorNombre) {
        this.productorNombre = productorNombre;
    }

    public BigDecimal getPrecioSugerido() {
        return precioSugerido;
    }

    public void setPrecioSugerido(BigDecimal precioSugerido) {
        this.precioSugerido = precioSugerido;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
