package com.entrecopas.financiero.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Contenedor raíz que agrupa los KPIs globales, el desglose por lote y el desglose por producto.
 */
public class DashboardFinancieroDTO {

    private FinancieroKPIsDTO kpis;
    private List<DesgloseLoteFinancieroDTO> desglosePorLote;
    private List<DesgloseProductoFinancieroDTO> desgloseProductos;

    public DashboardFinancieroDTO() {
        this.kpis = new FinancieroKPIsDTO();
        this.desglosePorLote = new ArrayList<>();
        this.desgloseProductos = new ArrayList<>();
    }

    public DashboardFinancieroDTO(FinancieroKPIsDTO kpis,
                                  List<DesgloseLoteFinancieroDTO> desglosePorLote,
                                  List<DesgloseProductoFinancieroDTO> desgloseProductos) {
        this.kpis = kpis != null ? kpis : new FinancieroKPIsDTO();
        this.desglosePorLote = desglosePorLote != null ? desglosePorLote : new ArrayList<>();
        this.desgloseProductos = desgloseProductos != null ? desgloseProductos : new ArrayList<>();
    }

    public FinancieroKPIsDTO getKpis() {
        return kpis;
    }

    public void setKpis(FinancieroKPIsDTO kpis) {
        this.kpis = kpis;
    }

    public List<DesgloseLoteFinancieroDTO> getDesglosePorLote() {
        return desglosePorLote;
    }

    public void setDesglosePorLote(List<DesgloseLoteFinancieroDTO> desglosePorLote) {
        this.desglosePorLote = desglosePorLote;
    }

    public List<DesgloseProductoFinancieroDTO> getDesgloseProductos() {
        return desgloseProductos;
    }

    public void setDesgloseProductos(List<DesgloseProductoFinancieroDTO> desgloseProductos) {
        this.desgloseProductos = desgloseProductos;
    }
}
