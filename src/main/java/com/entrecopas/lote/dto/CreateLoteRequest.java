package com.entrecopas.lote.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Petición para registrar un nuevo lote de producción artesanal.
 */
public class CreateLoteRequest {

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La fecha de producción es obligatoria")
    @PastOrPresent(message = "La fecha de producción no puede ser futura")
    private LocalDate fechaProduccion;

    @NotNull(message = "El volumen en litros es obligatorio")
    @Positive(message = "El volumen en litros debe ser mayor a cero")
    private BigDecimal volumenLitros;

    @NotNull(message = "La merma en litros es obligatoria")
    @PositiveOrZero(message = "La merma no puede ser negativa")
    private BigDecimal mermaLitros;

    @NotNull(message = "El costo total es obligatorio")
    @PositiveOrZero(message = "El costo total no puede ser negativo")
    private BigDecimal costoTotal;

    @NotNull(message = "El pH es obligatorio")
    @DecimalMin(value = "2.0", message = "El pH no puede ser menor a 2.0")
    @DecimalMax(value = "6.0", message = "El pH no puede ser mayor a 6.0")
    private Double ph;

    @NotNull(message = "La graduación alcohólica es obligatoria")
    @DecimalMin(value = "0.0", message = "La graduación alcohólica no puede ser negativa")
    @DecimalMax(value = "30.0", message = "La graduación alcohólica máxima permitida es 30%")
    private Double graduacionAlcoholica;

    @NotNull(message = "La acidez total es obligatoria")
    @Positive(message = "La acidez total debe ser mayor a cero")
    private Double acidezTotalGl;

    @NotNull(message = "La densidad es obligatoria")
    @Positive(message = "La densidad debe ser mayor a cero")
    private Double densidad;

    public CreateLoteRequest() {
    }

    public CreateLoteRequest(Long productoId, LocalDate fechaProduccion, BigDecimal volumenLitros,
                             BigDecimal mermaLitros, BigDecimal costoTotal, Double ph,
                             Double graduacionAlcoholica, Double acidezTotalGl, Double densidad) {
        this.productoId = productoId;
        this.fechaProduccion = fechaProduccion;
        this.volumenLitros = volumenLitros;
        this.mermaLitros = mermaLitros;
        this.costoTotal = costoTotal;
        this.ph = ph;
        this.graduacionAlcoholica = graduacionAlcoholica;
        this.acidezTotalGl = acidezTotalGl;
        this.densidad = densidad;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
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

    public BigDecimal getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(BigDecimal costoTotal) {
        this.costoTotal = costoTotal;
    }

    public Double getPh() {
        return ph;
    }

    public void setPh(Double ph) {
        this.ph = ph;
    }

    public Double getGraduacionAlcoholica() {
        return graduacionAlcoholica;
    }

    public void setGraduacionAlcoholica(Double graduacionAlcoholica) {
        this.graduacionAlcoholica = graduacionAlcoholica;
    }

    public Double getAcidezTotalGl() {
        return acidezTotalGl;
    }

    public void setAcidezTotalGl(Double acidezTotalGl) {
        this.acidezTotalGl = acidezTotalGl;
    }

    public Double getDensidad() {
        return densidad;
    }

    public void setDensidad(Double densidad) {
        this.densidad = densidad;
    }
}
