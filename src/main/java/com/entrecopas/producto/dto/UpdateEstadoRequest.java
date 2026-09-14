package com.entrecopas.producto.dto;

import com.entrecopas.producto.model.EstadoProducto;
import jakarta.validation.constraints.NotNull;

/**
 * Petición para actualización del estado de un producto.
 */
public class UpdateEstadoRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoProducto nuevoEstado;

    public UpdateEstadoRequest() {
    }

    public UpdateEstadoRequest(EstadoProducto nuevoEstado) {
        this.nuevoEstado = nuevoEstado;
    }

    public EstadoProducto getNuevoEstado() {
        return nuevoEstado;
    }

    public void setNuevoEstado(EstadoProducto nuevoEstado) {
        this.nuevoEstado = nuevoEstado;
    }
}
