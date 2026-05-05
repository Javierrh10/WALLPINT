package com.wallpint.wallpint.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Esta clase representa a un cliente en el sistema. Hereda de la clase Usuario, lo que significa que un cliente
 * es un tipo específico de usuario con atributos adicionales como dirección y fecha de registro.
 *
 * @Autor: Javier Raposo Huelva
 * @Version: 2026:04
 */

@Entity
@Table(name = "clientes")
public class Cliente extends Usuario {

    @Column(nullable = true)
    private String direccion;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    // =============== Constructores ===============
    // Constructor vacío
    public Cliente() {
        super(); // Llama al constructor de Usuario para inicializar los campos heredados
        this.setRol(Rol.CLIENTE); // Establece el rol del cliente
        this.fechaRegistro = LocalDateTime.now();
    }

    // =============== Getters y Setters ===============
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
