package com.entrecopas.financiero.dto;

import java.math.BigDecimal;

/**
 * DTO que encapsula los 4 indicadores clave agregados (KPIs) del productor.
 */
public class FinancieroKPIsDTO {

    private BigDecimal costoTotalAcumulado;
    private BigDecimal volumenTotalLitros;
    private BigDecimal mermaTotalLitros;
    private BigDecimal volumenNetoLitros;
    private BigDecimal porcentajeMermaGlobal;
    private BigDecimal costoPromedioPorLitro;
    private int lotesContabilizados;

    public FinancieroKPIsDTO() {
        this.costoTotalAcumulado = BigDecimal.ZERO;
        this.volumenTotalLitros = BigDecimal.ZERO;
        this.mermaTotalLitros = BigDecimal.ZERO;
        this.volumenNetoLitros = BigDecimal.ZERO;
        this.porcentajeMermaGlobal = BigDecimal.ZERO;
        this.costoPromedioPorLitro = BigDecimal.ZERO;
        this.lotesContabilizados = 0;
    }

    public FinancieroKPIsDTO(BigDecimal costoTotalAcumulado,
                            BigDecimal volumenTotalLitros,
                            BigDecimal mermaTotalLitros,
                            BigDecimal volumenNetoLitros,
                            BigDecimal porcentajeMermaGlobal,
                            BigDecimal costoPromedioPorLitro,
                            int lotesContabilizados) {
        this.costoTotalAcumulado = costoTotalAcumulado;
        this.volumenTotalLitros = volumenTotalLitros;
        this.mermaTotalLitros = mermaTotalLitros;
        this.volumenNetoLitros = volumenNetoLitros;
        this.porcentajeMermaGlobal = porcentajeMermaGlobal;
        this.costoPromedioPorLitro = costoPromedioPorLitro;
        this.lotesContabilizados = lotesContabilizados;
    }

    public BigDecimal getCostoTotalAcumulado() {
        return costoTotalAcumulado;
    }

    public void setCostoTotalAcumulado(BigDecimal costoTotalAcumulado) {
        this.costoTotalAcumulado = costoTotalAcumulado;
    }

    public BigDecimal getVolumenTotalLitros() {
        return volumenTotalLitros;
    }

    public void setVolumenTotalLitros(BigDecimal volumenTotalLitros) {
        this.volumenTotalLitros = volumenTotalLitros;
    }

    public BigDecimal getMermaTotalLitros() {
        return mermaTotalLitros;
    }

    public void setMermaTotalLitros(BigDecimal mermaTotalLitros) {
        this.mermaTotalLitros = mermaTotalLitros;
    }

    public BigDecimal getVolumenNetoLitros() {
        return volumenNetoLitros;
    }

    public void setVolumenNetoLitros(BigDecimal volumenNetoLitros) {
        this.volumenNetoLitros = volumenNetoLitros;
    }

    public BigDecimal getPorcentajeMermaGlobal() {
        return porcentajeMermaGlobal;
    }

    public void setPorcentajeMermaGlobal(BigDecimal porcentajeMermaGlobal) {
        this.porcentajeMermaGlobal = porcentajeMermaGlobal;
    }

    public BigDecimal getCostoPromedioPorLitro() {
        return costoPromedioPorLitro;
    }

    public void setCostoPromedioPorLitro(BigDecimal costoPromedioPorLitro) {
        this.costoPromedioPorLitro = costoPromedioPorLitro;
    }

    public int getLotesContabilizados() {
        return lotesContabilizados;
    }

    public void setLotesContabilizados(int lotesContabilizados) {
        this.lotesContabilizados = lotesContabilizados;
    }
}
