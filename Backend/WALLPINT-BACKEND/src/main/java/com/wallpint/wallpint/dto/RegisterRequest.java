package com.wallpint.wallpint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank private String nombre;
    @NotBlank private String apellidos;
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 9, max = 9) private String telefono;
    @NotBlank @Size(min = 6) private String password;
    private String direccion; // opcional para Cliente

    public String getNombre() { return nombre; }
    public void setNombre(String n) { this.nombre = n; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String a) { this.apellidos = a; }
    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String t) { this.telefono = t; }
    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String d) { this.direccion = d; }
}