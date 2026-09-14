package com.entrecopas.producto.dto;

import com.entrecopas.producto.model.EstadoProducto;
import com.entrecopas.producto.model.Producto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de transferencia para datos de Producto y Productor asociado.
 */
public class ProductoDTO {

    private Long id;
    private Long productorId;
    private String productorNombre;
    private String productorUbicacion;
    private String nombre;
    private String tipoBebida;
    private String descripcion;
    private String presentacion;
    private BigDecimal precio;
    private EstadoProducto estado;
    private LocalDateTime createdAt;

    public ProductoDTO() {
    }

    public static ProductoDTO fromEntity(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        if (producto.getProductor() != null) {
            dto.setProductorId(producto.getProductor().getId());
            dto.setProductorNombre(producto.getProductor().getNombreComercial());
            dto.setProductorUbicacion(producto.getProductor().getUbicacionOrigen());
        }
        dto.setNombre(producto.getNombre());
        dto.setTipoBebida(producto.getTipoBebida());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPresentacion(producto.getPresentacion());
        dto.setPrecio(producto.getPrecio());
        dto.setEstado(producto.getEstado());
        dto.setCreatedAt(producto.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductorId() {
        return productorId;
    }

    public void setProductorId(Long productorId) {
        this.productorId = productorId;
    }

    public String getProductorNombre() {
        return productorNombre;
    }

    public void setProductorNombre(String productorNombre) {
        this.productorNombre = productorNombre;
    }

    public String getProductorUbicacion() {
        return productorUbicacion;
    }

    public void setProductorUbicacion(String productorUbicacion) {
        this.productorUbicacion = productorUbicacion;
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

    public EstadoProducto getEstado() {
        return estado;
    }

    public void setEstado(EstadoProducto estado) {
        this.estado = estado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
