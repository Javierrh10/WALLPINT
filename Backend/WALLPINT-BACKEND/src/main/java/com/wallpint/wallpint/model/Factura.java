package com.wallpint.wallpint.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Esta clase ...
 *
 * @Autor: Javier Raposo Huelva
 * @Version: 2026:04
 */
@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroFactura; // Ej: "FAC-2026-0001"

    @Column(nullable = false)
    private LocalDateTime fechaEmision;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false)
    private String estado; // Ej: "PENDIENTE", "PAGADA"

    // --- RELACIÓN CON PRESUPUESTO ---
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presupuesto_id", nullable = false)
    private Presupuesto presupuesto;

    // =============== Constructores ===============
    public Factura() {
        this.fechaEmision = LocalDateTime.now();
        this.estado = "PENDIENTE";
    }

    // =============== Getters y Setters ===============
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Presupuesto getPresupuesto() {
        return presupuesto;
    }
    public void setPresupuesto(Presupuesto presupuesto) {
        this.presupuesto = presupuesto;
    }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Double getTotal() {
        return total;
    }
    public void setTotal(Double total) {
        this.total = total;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }
    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }
    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }
}
