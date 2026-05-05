package com.wallpint.wallpint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos editables del perfil del usuario logueado.
 * Email y contraseña tienen flujos separados por seguridad.
 */
public class EditarPerfilRequest {

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellidos;

    @NotBlank
    @Size(min = 9, max = 9)
    private String telefono;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
