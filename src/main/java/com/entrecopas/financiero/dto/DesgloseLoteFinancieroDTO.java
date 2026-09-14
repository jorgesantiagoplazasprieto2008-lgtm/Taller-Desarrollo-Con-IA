package com.entrecopas.financiero.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para auditoría y desglose analítico por lote de producción individual.
 */
public class DesgloseLoteFinancieroDTO {

    private Long loteId;
    private String codigoTrazabilidad;
    private String productoNombre;
    private String presentacion;
    private LocalDate fechaProduccion;
    private BigDecimal volumenLitros;
    private BigDecimal mermaLitros;
    private BigDecimal mermaPorcentaje;
    private BigDecimal volumenNeto;
    private BigDecimal costoTotal;
    private BigDecimal costoPorLitro;
    private int unidadesEstimadas;
    private BigDecimal costoPorUnidad;
    private BigDecimal precioVentaUnitario;
    private BigDecimal margenBrutoPorcentaje;

    public DesgloseLoteFinancieroDTO() {
    }

    public DesgloseLoteFinancieroDTO(Long loteId,
                                    String codigoTrazabilidad,
                                    String productoNombre,
                                    String presentacion,
                                    LocalDate fechaProduccion,
                                    BigDecimal volumenLitros,
                                    BigDecimal mermaLitros,
                                    BigDecimal mermaPorcentaje,
                                    BigDecimal volumenNeto,
                                    BigDecimal costoTotal,
                                    BigDecimal costoPorLitro,
                                    int unidadesEstimadas,
                                    BigDecimal costoPorUnidad,
                                    BigDecimal precioVentaUnitario,
                                    BigDecimal margenBrutoPorcentaje) {
        this.loteId = loteId;
        this.codigoTrazabilidad = codigoTrazabilidad;
        this.productoNombre = productoNombre;
        this.presentacion = presentacion;
        this.fechaProduccion = fechaProduccion;
        this.volumenLitros = volumenLitros;
        this.mermaLitros = mermaLitros;
        this.mermaPorcentaje = mermaPorcentaje;
        this.volumenNeto = volumenNeto;
        this.costoTotal = costoTotal;
        this.costoPorLitro = costoPorLitro;
        this.unidadesEstimadas = unidadesEstimadas;
        this.costoPorUnidad = costoPorUnidad;
        this.precioVentaUnitario = precioVentaUnitario;
        this.margenBrutoPorcentaje = margenBrutoPorcentaje;
    }

    public Long getLoteId() {
        return loteId;
    }

    public void setLoteId(Long loteId) {
        this.loteId = loteId;
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

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
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

    public BigDecimal getMermaPorcentaje() {
        return mermaPorcentaje;
    }

    public void setMermaPorcentaje(BigDecimal mermaPorcentaje) {
        this.mermaPorcentaje = mermaPorcentaje;
    }

    public BigDecimal getVolumenNeto() {
        return volumenNeto;
    }

    public void setVolumenNeto(BigDecimal volumenNeto) {
        this.volumenNeto = volumenNeto;
    }

    public BigDecimal getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(BigDecimal costoTotal) {
        this.costoTotal = costoTotal;
    }

    public BigDecimal getCostoPorLitro() {
        return costoPorLitro;
    }

    public void setCostoPorLitro(BigDecimal costoPorLitro) {
        this.costoPorLitro = costoPorLitro;
    }

    public int getUnidadesEstimadas() {
        return unidadesEstimadas;
    }

    public void setUnidadesEstimadas(int unidadesEstimadas) {
        this.unidadesEstimadas = unidadesEstimadas;
    }

    public BigDecimal getCostoPorUnidad() {
        return costoPorUnidad;
    }

    public void setCostoPorUnidad(BigDecimal costoPorUnidad) {
        this.costoPorUnidad = costoPorUnidad;
    }

    public BigDecimal getPrecioVentaUnitario() {
        return precioVentaUnitario;
    }

    public void setPrecioVentaUnitario(BigDecimal precioVentaUnitario) {
        this.precioVentaUnitario = precioVentaUnitario;
    }

    public BigDecimal getMargenBrutoPorcentaje() {
        return margenBrutoPorcentaje;
    }

    public void setMargenBrutoPorcentaje(BigDecimal margenBrutoPorcentaje) {
        this.margenBrutoPorcentaje = margenBrutoPorcentaje;
    }
}
