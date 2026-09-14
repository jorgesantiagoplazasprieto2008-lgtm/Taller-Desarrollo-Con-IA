package com.entrecopas.producto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Petición para registrar un nuevo producto artesanal.
 * NOTA: El productorId NUNCA se recibe del cliente; se extrae de la sesión autenticada.
 */
public class CreateProductoRequest {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 120, message = "El nombre no puede exceder 120 caracteres")
    private String nombre;

    @NotBlank(message = "El tipo de bebida es obligatorio")
    @Size(max = 50, message = "El tipo de bebida no puede exceder 50 caracteres")
    private String tipoBebida;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotBlank(message = "La presentación es obligatoria")
    @Size(max = 50, message = "La presentación no puede exceder 50 caracteres")
    private String presentacion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser un valor positivo")
    private BigDecimal precio;

    public CreateProductoRequest() {
    }

    public CreateProductoRequest(String nombre, String tipoBebida, String descripcion,
                                 String presentacion, BigDecimal precio) {
        this.nombre = nombre;
        this.tipoBebida = tipoBebida;
        this.descripcion = descripcion;
        this.presentacion = presentacion;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoBebida() {
        return tipoBebida;
    }

    public void setTipoBebida(String tipoBebida) {
        this.tipoBebida = tipoBebida;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}
