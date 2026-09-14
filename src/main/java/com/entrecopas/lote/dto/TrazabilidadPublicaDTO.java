package com.entrecopas.lote.dto;

import com.entrecopas.lote.model.Lote;
import com.entrecopas.lote.model.ParametrosAnaliticos;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para la consulta pública de trazabilidad certificada.
 * REGLA ESTRICTA DE SEGURIDAD (PRD 1.2): Enmascara y excluye secretos industriales
 * como 'costo_total' y 'merma_litros'.
 */
public class TrazabilidadPublicaDTO {

    private String codigoTrazabilidad;
    private String productoNombre;
    private String tipoBebida;
    private String presentacion;
    private String descripcion;
    private String productorNombre;
    private String productorUbicacion;
    private String registroSanitario;
    private LocalDate fechaProduccion;
    private ParametrosAnaliticos parametrosAnaliticos;
    private LocalDateTime fechaConsulta;
    private boolean certificadoValido;

    public TrazabilidadPublicaDTO() {
    }

    public static TrazabilidadPublicaDTO fromEntity(Lote lote) {
        TrazabilidadPublicaDTO dto = new TrazabilidadPublicaDTO();
        dto.setCodigoTrazabilidad(lote.getCodigoTrazabilidad());
        if (lote.getProducto() != null) {
            dto.setProductoNombre(lote.getProducto().getNombre());
            dto.setTipoBebida(lote.getProducto().getTipoBebida());
            dto.setPresentacion(lote.getProducto().getPresentacion());
            dto.setDescripcion(lote.getProducto().getDescripcion());

            if (lote.getProducto().getProductor() != null) {
                dto.setProductorNombre(lote.getProducto().getProductor().getNombreComercial());
                dto.setProductorUbicacion(lote.getProducto().getProductor().getUbicacionOrigen());
                dto.setRegistroSanitario(lote.getProducto().getProductor().getRegistroSanitario());
            }
        }
        dto.setFechaProduccion(lote.getFechaProduccion());
        dto.setParametrosAnaliticos(lote.getParametrosAnaliticos());
        dto.setFechaConsulta(LocalDateTime.now());
        dto.setCertificadoValido(true);
        return dto;
    }

    public String getCodigoTrazabilidad() {
        return codigoTrazabilidad;
    }

    public void setCodigoTrazabilidad(String codigoTrazabilidad) {
        this.codigoTrazabilidad = codigoTrazabilidad;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public String getRegistroSanitario() {
        return registroSanitario;
    }

    public void setRegistroSanitario(String registroSanitario) {
        this.registroSanitario = registroSanitario;
    }

    public LocalDate getFechaProduccion() {
        return fechaProduccion;
    }

    public void setFechaProduccion(LocalDate fechaProduccion) {
        this.fechaProduccion = fechaProduccion;
    }

    public ParametrosAnaliticos getParametrosAnaliticos() {
        return parametrosAnaliticos;
    }

    public void setParametrosAnaliticos(ParametrosAnaliticos parametrosAnaliticos) {
        this.parametrosAnaliticos = parametrosAnaliticos;
    }

    public LocalDateTime getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(LocalDateTime fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }

    public boolean isCertificadoValido() {
        return certificadoValido;
    }

    public void setCertificadoValido(boolean certificadoValido) {
        this.certificadoValido = certificadoValido;
    }
}
