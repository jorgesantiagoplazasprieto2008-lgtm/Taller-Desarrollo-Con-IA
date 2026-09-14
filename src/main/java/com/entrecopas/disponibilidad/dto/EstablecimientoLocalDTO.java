package com.entrecopas.disponibilidad.dto;

import com.entrecopas.disponibilidad.model.Disponibilidad;
import java.time.LocalDateTime;

/**
 * DTO para presentar los locales gastronómicos donde un producto artesanal
 * se encuentra actualmente disponible en barra.
 */
public class EstablecimientoLocalDTO {

    private Long establecimientoId;
    private String nombre;
    private String direccion;
    private String ciudad;
    private boolean disponible;
    private LocalDateTime updatedAt;

    public EstablecimientoLocalDTO() {
    }

    public EstablecimientoLocalDTO(Long establecimientoId,
                                   String nombre,
                                   String direccion,
                                   String ciudad,
                                   boolean disponible,
                                   LocalDateTime updatedAt) {
        this.establecimientoId = establecimientoId;
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.disponible = disponible;
        this.updatedAt = updatedAt;
    }

    public static EstablecimientoLocalDTO fromEntity(Disponibilidad disp) {
        return new EstablecimientoLocalDTO(
                disp.getEstablecimiento().getId(),
                disp.getEstablecimiento().getNombre(),
                disp.getEstablecimiento().getDireccion(),
                disp.getEstablecimiento().getCiudad(),
                disp.getDisponible(),
                disp.getUpdatedAt()
        );
    }

    public Long getEstablecimientoId() {
        return establecimientoId;
    }

    public void setEstablecimientoId(Long establecimientoId) {
        this.establecimientoId = establecimientoId;
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

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
