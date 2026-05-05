package com.wallpint.wallpint.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Esta clase representa a un usuario en el sistema. Es la clase base para otros tipos de usuarios
 * como Cliente y Pintor, y contiene atributos comunes a todos los usuarios, como nombre, apellidos, email,
 * teléfono, contraseña y rol.
 *
 * @author : Javier Raposo Huelva
 * @version : 2026:04
 */
@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 9)
    private String telefono;

    @JsonIgnore
    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private Boolean activo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;


    // ============================ Constructores ============================
    public Usuario() {
        this.activo = true; // Por defecto, el usuario está activo
    }

    // ============================ Getters y Setters =========================
    public Rol getRol() {
        return rol;
    }
    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Boolean getActivo() {
        return activo;
    }
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getApellidos() {
        return apellidos;
    }
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
