package com.entrecopas.usuario.dto;

import com.entrecopas.usuario.model.Rol;
import com.entrecopas.usuario.model.Usuario;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO para enviar información pública y de perfil de un Usuario autenticado.
 */
public class UsuarioResponseDTO {

    private Long id;
    private String email;
    private String nombreCompleto;
    private String telefono;
    private Set<String> roles;

    public UsuarioResponseDTO() {
    }

    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setEmail(usuario.getEmail());
        dto.setNombreCompleto(usuario.getNombreCompleto());
        dto.setTelefono(usuario.getTelefono());
        dto.setRoles(usuario.getRoles().stream().map(Rol::getNombre).collect(Collectors.toSet()));
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
