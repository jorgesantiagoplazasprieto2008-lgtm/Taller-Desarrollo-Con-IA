package com.entrecopas.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Petición para registrar un nuevo usuario con rol específico.
 */
public class RegistroRequest {

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato de correo no es válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    private String telefono;

    @NotBlank(message = "El rol es obligatorio")
    private String rol; // ROLE_PRODUCTOR, ROLE_HOSTELERIA, ROLE_CONSUMIDOR

    // Datos opcionales / requeridos si el rol es ROLE_PRODUCTOR
    private String nombreComercial;
    private String ubicacionOrigen;
    private String registroSanitario;
    private String descripcionHistoria;

    // Datos opcionales si el rol es ROLE_HOSTELERIA
    private String nombreEstablecimiento;
    private String direccionEstablecimiento;
    private String ciudadEstablecimiento;

    public RegistroRequest() {
    }

    public RegistroRequest(String email, String password, String nombreCompleto, String rol) {
        this.email = email;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getUbicacionOrigen() {
        return ubicacionOrigen;
    }

    public void setUbicacionOrigen(String ubicacionOrigen) {
        this.ubicacionOrigen = ubicacionOrigen;
    }

    public String getRegistroSanitario() {
        return registroSanitario;
    }

    public void setRegistroSanitario(String registroSanitario) {
        this.registroSanitario = registroSanitario;
    }

    public String getDescripcionHistoria() {
        return descripcionHistoria;
    }

    public void setDescripcionHistoria(String descripcionHistoria) {
        this.descripcionHistoria = descripcionHistoria;
    }

    public String getNombreEstablecimiento() {
        return nombreEstablecimiento;
    }

    public void setNombreEstablecimiento(String nombreEstablecimiento) {
        this.nombreEstablecimiento = nombreEstablecimiento;
    }

    public String getDireccionEstablecimiento() {
        return direccionEstablecimiento;
    }

    public void setDireccionEstablecimiento(String direccionEstablecimiento) {
        this.direccionEstablecimiento = direccionEstablecimiento;
    }

    public String getCiudadEstablecimiento() {
        return ciudadEstablecimiento;
    }

    public void setCiudadEstablecimiento(String ciudadEstablecimiento) {
        this.ciudadEstablecimiento = ciudadEstablecimiento;
    }
}
