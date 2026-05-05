package com.wallpint.wallpint.model;

import jakarta.persistence.*;

/**
 * Esta clase ...
 *
 * @Autor: Javier Raposo Huelva
 * @Version: 2026:04
 */
@Entity
@Table(name = "pintores")
public class Pintor extends Usuario {

    // =============== Constructores ===============
    public Pintor() {
        super(); // Llama al constructor de Usuario para inicializar los campos heredados
        this.setRol(Rol.PINTOR); // Establece el rol del pintor
    }

    // =============== Getters y Setters ===============
}
