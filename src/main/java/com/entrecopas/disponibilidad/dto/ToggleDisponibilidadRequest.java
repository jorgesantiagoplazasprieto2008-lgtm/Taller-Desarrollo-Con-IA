package com.entrecopas.disponibilidad.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Petición para alternar el estado de disponibilidad de un producto en un local de hostelería.
 */
public class ToggleDisponibilidadRequest {

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    @NotNull(message = "El estado de disponibilidad es obligatorio")
    private Boolean disponible;

    public ToggleDisponibilidadRequest() {
    }

    public ToggleDisponibilidadRequest(Long productoId, Boolean disponible) {
        this.productoId = productoId;
        this.disponible = disponible;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }
}
