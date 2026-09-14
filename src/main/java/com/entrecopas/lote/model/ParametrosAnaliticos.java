package com.entrecopas.lote.model;

import java.io.Serializable;

/**
 * Parámetros físico-químicos analíticos de laboratorio para un lote de bebida artesanal.
 * Mapeado a la columna nativa JSON en la base de datos.
 */
public class ParametrosAnaliticos implements Serializable {

    private static final long serialVersionUID = 1L;

    private Double ph;
    private Double graduacionAlcoholica;
    private Double acidezTotalGl;
    private Double densidad;

    public ParametrosAnaliticos() {
    }

    public ParametrosAnaliticos(Double ph, Double graduacionAlcoholica, Double acidezTotalGl, Double densidad) {
        this.ph = ph;
        this.graduacionAlcoholica = graduacionAlcoholica;
        this.acidezTotalGl = acidezTotalGl;
        this.densidad = densidad;
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
