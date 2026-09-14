package com.entrecopas.establecimiento.dto;

import com.entrecopas.establecimiento.model.Establecimiento;

/**
 * DTO para la información de un establecimiento de hostelería.
 */
public class EstablecimientoDTO {

    private Long id;
    private Long usuarioId;
    private String nombre;
    private String direccion;
    private String ciudad;
    private Boolean activo;

    public EstablecimientoDTO() {
    }

    public static EstablecimientoDTO fromEntity(Establecimiento est) {
        EstablecimientoDTO dto = new EstablecimientoDTO();
        dto.setId(est.getId());
        if (est.getUsuario() != null) {
            dto.setUsuarioId(est.getUsuario().getId());
        }
        dto.setNombre(est.getNombre());
        dto.setDireccion(est.getDireccion());
        dto.setCiudad(est.getCiudad());
        dto.setActivo(est.getActivo());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
