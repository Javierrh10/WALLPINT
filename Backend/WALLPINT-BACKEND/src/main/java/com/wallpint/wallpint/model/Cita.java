package com.wallpint.wallpint.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase ...
 *
 * @Autor: Javier Raposo Huelva
 * @Version: 2026:04
 */
@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private String franja; // Ej: "Mañana", "Tarde"

    @Column(nullable = false)
    private String estado; // Ej: "PROGRAMADA", "CANCELADA", "COMPLETADA", "EN_CURSO"

    @Column(length = 500)
    private String notasInternas; // Para uso interno del pintor

    // --- RELACIÓN CON CLIENTE ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // --- RELACIÓN CON PRESUPUESTO ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presupuesto_id")
    private Presupuesto presupuesto;

    // --- RELACIÓN CON PINTOR ---
    @ManyToMany
    @JoinTable(
        name = "cita_pintor",
        joinColumns = @JoinColumn(name = "cita_id"),
        inverseJoinColumns = @JoinColumn(name = "pintor_id")
    )
    private List<Pintor> pintores = new ArrayList<>();


    // =============== Constructores ===============
    public Cita() {}

    // =============== Getters y Setters ===============
    public List<Pintor> getPintores() {
        return pintores;
    }
    public void setPintores(List<Pintor> pintores) {
        this.pintores = pintores;
    }

    public Presupuesto getPresupuesto() {
        return presupuesto;
    }
    public void setPresupuesto(Presupuesto presupuesto) {
        this.presupuesto = presupuesto;
    }

    public Cliente getCliente() {
        return cliente;
    }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getNotasInternas() {
        return notasInternas;
    }
    public void setNotasInternas(String notasInternas) {
        this.notasInternas = notasInternas;
    }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFranja() {
        return franja;
    }
    public void setFranja(String franja) {
        this.franja = franja;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}
