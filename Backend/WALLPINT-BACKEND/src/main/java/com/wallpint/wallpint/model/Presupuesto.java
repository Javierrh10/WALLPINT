package com.wallpint.wallpint.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "presupuestos")
public class Presupuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String referencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnore
    private Cliente cliente;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private TipoPresupuesto tipo = TipoPresupuesto.ORIENTATIVO;

    @Enumerated(EnumType.STRING)
    private EstadoPresupuesto estado = EstadoPresupuesto.ORIENTATIVO;

    // Resultados del cálculo
    @Column(name = "total_m2")
    private Double totalM2;

    @Column(name = "litros_pintura")
    private Integer litrosPintura;

    @Column(name = "horas_estimadas")
    private Double horasEstimadas;

    @Column(name = "num_pintores")
    private Integer numPintores;

    @Column(name = "coste_materiales")
    private Double costeMateriales;

    @Column(name = "coste_mano_obra")
    private Double costeManoObra;

    private Double iva;

    private Double total;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Estancia> estancias = new ArrayList<>();

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String r) { this.referencia = r; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente c) { this.cliente = c; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime f) { this.fechaSolicitud = f; }
    public TipoPresupuesto getTipo() { return tipo; }
    public void setTipo(TipoPresupuesto t) { this.tipo = t; }
    public EstadoPresupuesto getEstado() { return estado; }
    public void setEstado(EstadoPresupuesto e) { this.estado = e; }
    public Double getTotalM2() { return totalM2; }
    public void setTotalM2(Double t) { this.totalM2 = t; }
    public Integer getLitrosPintura() { return litrosPintura; }
    public void setLitrosPintura(Integer l) { this.litrosPintura = l; }
    public Double getHorasEstimadas() { return horasEstimadas; }
    public void setHorasEstimadas(Double h) { this.horasEstimadas = h; }
    public Integer getNumPintores() { return numPintores; }
    public void setNumPintores(Integer n) { this.numPintores = n; }
    public Double getCosteMateriales() { return costeMateriales; }
    public void setCosteMateriales(Double c) { this.costeMateriales = c; }
    public Double getCosteManoObra() { return costeManoObra; }
    public void setCosteManoObra(Double c) { this.costeManoObra = c; }
    public Double getIva() { return iva; }
    public void setIva(Double i) { this.iva = i; }
    public Double getTotal() { return total; }
    public void setTotal(Double t) { this.total = t; }
    public List<Estancia> getEstancias() { return estancias; }
    public void setEstancias(List<Estancia> e) { this.estancias = e; }
}