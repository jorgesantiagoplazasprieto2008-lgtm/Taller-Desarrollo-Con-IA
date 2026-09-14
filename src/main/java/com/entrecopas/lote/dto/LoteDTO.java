package com.entrecopas.lote.dto;

import com.entrecopas.lote.model.Lote;
import com.entrecopas.lote.model.ParametrosAnaliticos;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para la gestión privada de lotes por parte del productor y administración.
 */
public class LoteDTO {

    private Long id;
    private Long productoId;
    private String productoNombre;
    private String tipoBebida;
    private String codigoTrazabilidad;
    private LocalDate fechaProduccion;
    private BigDecimal volumenLitros;
    private BigDecimal mermaLitros;
    private BigDecimal porcentajeMerma;
    private BigDecimal costoTotal;
    private BigDecimal costoPorLitro;
    private ParametrosAnaliticos parametrosAnaliticos;
    private LocalDateTime createdAt;

    public LoteDTO() {
    }

    public static LoteDTO fromEntity(Lote lote) {
        LoteDTO dto = new LoteDTO();
        dto.setId(lote.getId());
        if (lote.getProducto() != null) {
            dto.setProductoId(lote.getProducto().getId());
            dto.setProductoNombre(lote.getProducto().getNombre());
            dto.setTipoBebida(lote.getProducto().getTipoBebida());
        }
        dto.setCodigoTrazabilidad(lote.getCodigoTrazabilidad());
        dto.setFechaProduccion(lote.getFechaProduccion());
        dto.setVolumenLitros(lote.getVolumenLitros());
        dto.setMermaLitros(lote.getMermaLitros());

        // Cálculo derivado de porcentaje de merma: (merma / volumen) * 100
        if (lote.getVolumenLitros() != null && lote.getVolumenLitros().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal pct = lote.getMermaLitros()
                    .multiply(new BigDecimal("100"))
                    .divide(lote.getVolumenLitros(), 2, RoundingMode.HALF_UP);
            dto.setPorcentajeMerma(pct);

            // Costo por litro útil
            BigDecimal volumenUtil = lote.getVolumenLitros().subtract(lote.getMermaLitros());
            if (volumenUtil.compareTo(BigDecimal.ZERO) > 0) {
                dto.setCostoPorLitro(lote.getCostoTotal().divide(volumenUtil, 2, RoundingMode.HALF_UP));
            } else {
                dto.setCostoPorLitro(BigDecimal.ZERO);
            }
        } else {
            dto.setPorcentajeMerma(BigDecimal.ZERO);
            dto.setCostoPorLitro(BigDecimal.ZERO);
        }

        dto.setCostoTotal(lote.getCostoTotal());
        dto.setParametrosAnaliticos(lote.getParametrosAnaliticos());
        dto.setCreatedAt(lote.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCodigoTrazabilidad() {
        return codigoTrazabilidad;
    }

    public void setCodigoTrazabilidad(String codigoTrazabilidad) {
        this.codigoTrazabilidad = codigoTrazabilidad;
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

    public BigDecimal getPorcentajeMerma() {
        return porcentajeMerma;
    }

    public void setPorcentajeMerma(BigDecimal porcentajeMerma) {
        this.porcentajeMerma = porcentajeMerma;
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

    public ParametrosAnaliticos getParametrosAnaliticos() {
        return parametrosAnaliticos;
    }

    public void setParametrosAnaliticos(ParametrosAnaliticos parametrosAnaliticos) {
        this.parametrosAnaliticos = parametrosAnaliticos;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
