package com.wallpint.wallpint.dto;

/**
 * Esta clase ...
 *
 * @author : Javier Raposo Huelva
 * @version : 2026:04
 */
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String apellidos;
    private String email;
    private String password;
    private String telefono;
    private String rol;

    // =============== Constructores ===============

    public UsuarioDTO() {
    }

    public UsuarioDTO(Long id, String nombre, String apellidos, String email, String password, String telefono, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.rol = rol;
    }

    // =============== Getters y Setters ===============
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
