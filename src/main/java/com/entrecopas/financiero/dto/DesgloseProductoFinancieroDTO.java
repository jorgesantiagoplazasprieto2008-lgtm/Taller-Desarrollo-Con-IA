package com.entrecopas.financiero.dto;

import java.math.BigDecimal;

/**
 * DTO para el desglose consolidado de rentabilidad y costos por producto.
 */
public class DesgloseProductoFinancieroDTO {

    private Long productoId;
    private String nombre;
    private String tipoBebida;
    private BigDecimal precio;
    private int lotesCount;
    private BigDecimal costoAcumulado;
    private BigDecimal volumenNetoTotal;
    private int unidadesEstimadasTotal;
    private BigDecimal margenPromedioPorcentaje;

    public DesgloseProductoFinancieroDTO() {
    }

    public DesgloseProductoFinancieroDTO(Long productoId,
                                        String nombre,
                                        String tipoBebida,
                                        BigDecimal precio,
                                        int lotesCount,
                                        BigDecimal costoAcumulado,
                                        BigDecimal volumenNetoTotal,
                                        int unidadesEstimadasTotal,
                                        BigDecimal margenPromedioPorcentaje) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.tipoBebida = tipoBebida;
        this.precio = precio;
        this.lotesCount = lotesCount;
        this.costoAcumulado = costoAcumulado;
        this.volumenNetoTotal = volumenNetoTotal;
        this.unidadesEstimadasTotal = unidadesEstimadasTotal;
        this.margenPromedioPorcentaje = margenPromedioPorcentaje;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
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

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getLotesCount() {
        return lotesCount;
    }

    public void setLotesCount(int lotesCount) {
        this.lotesCount = lotesCount;
    }

    public BigDecimal getCostoAcumulado() {
        return costoAcumulado;
    }

    public void setCostoAcumulado(BigDecimal costoAcumulado) {
        this.costoAcumulado = costoAcumulado;
    }

    public BigDecimal getVolumenNetoTotal() {
        return volumenNetoTotal;
    }

    public void setVolumenNetoTotal(BigDecimal volumenNetoTotal) {
        this.volumenNetoTotal = volumenNetoTotal;
    }

    public int getUnidadesEstimadasTotal() {
        return unidadesEstimadasTotal;
    }

    public void setUnidadesEstimadasTotal(int unidadesEstimadasTotal) {
        this.unidadesEstimadasTotal = unidadesEstimadasTotal;
    }

    public BigDecimal getMargenPromedioPorcentaje() {
        return margenPromedioPorcentaje;
    }

    public void setMargenPromedioPorcentaje(BigDecimal margenPromedioPorcentaje) {
        this.margenPromedioPorcentaje = margenPromedioPorcentaje;
    }
}
