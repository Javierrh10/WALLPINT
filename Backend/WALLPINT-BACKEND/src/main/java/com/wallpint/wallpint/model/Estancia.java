package com.wallpint.wallpint.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "estancias")
public class Estancia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Double ancho;

    @Column(nullable = false)
    private Double largo;

    @Column(nullable = false)
    private Double alto;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_paredes", nullable = false)
    private EstadoPared estadoParedes;

    @Column(name = "num_puertas", nullable = false)
    private Integer numPuertas = 0;

    @Column(name = "num_ventanas", nullable = false)
    private Integer numVentanas = 0;

    private String color;

    @Column(name = "num_capas", nullable = false)
    private Integer numCapas = 1;

    @Column(name = "incluir_techo", nullable = false)
    private Boolean incluirTecho = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presupuesto_id")
    @JsonBackReference
    private Presupuesto presupuesto;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String n) { this.nombre = n; }
    public Double getAncho() { return ancho; }
    public void setAncho(Double a) { this.ancho = a; }
    public Double getLargo() { return largo; }
    public void setLargo(Double l) { this.largo = l; }
    public Double getAlto() { return alto; }
    public void setAlto(Double a) { this.alto = a; }
    public EstadoPared getEstadoParedes() { return estadoParedes; }
    public void setEstadoParedes(EstadoPared e) { this.estadoParedes = e; }
    public Integer getNumPuertas() { return numPuertas; }
    public void setNumPuertas(Integer n) { this.numPuertas = n; }
    public Integer getNumVentanas() { return numVentanas; }
    public void setNumVentanas(Integer n) { this.numVentanas = n; }
    public String getColor() { return color; }
    public void setColor(String c) { this.color = c; }
    public Integer getNumCapas() { return numCapas; }
    public void setNumCapas(Integer n) { this.numCapas = n; }
    public Boolean getIncluirTecho() { return incluirTecho; }
    public void setIncluirTecho(Boolean i) { this.incluirTecho = i; }
    public Presupuesto getPresupuesto() { return presupuesto; }
    public void setPresupuesto(Presupuesto p) { this.presupuesto = p; }
}