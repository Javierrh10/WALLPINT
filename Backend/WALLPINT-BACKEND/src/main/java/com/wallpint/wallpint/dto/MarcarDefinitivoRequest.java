package com.wallpint.wallpint.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO usado por el admin/pintor tras la visita técnica para fijar los datos
 * definitivos del presupuesto. Todos los campos técnicos son opcionales: si
 * no se envían, se mantiene el valor calculado en el orientativo.
 *
 * Si se envían 'costeMateriales' y 'costeManoObra', el servicio recalcula el
 * total a partir de ellos (subtotal * 1.21). Si solo se envía 'nuevoTotal',
 * se distribuye automáticamente entre materiales (40%) y mano de obra (60%).
 */
public class MarcarDefinitivoRequest {

    @Positive
    private Double nuevoTotal;

    @PositiveOrZero
    private Double totalM2;

    @PositiveOrZero
    private Integer litrosPintura;

    @PositiveOrZero
    private Double horasEstimadas;

    @PositiveOrZero
    private Integer numPintores;

    @PositiveOrZero
    private Double costeMateriales;

    @PositiveOrZero
    private Double costeManoObra;

    private String notas;

    public Double getNuevoTotal() { return nuevoTotal; }
    public void setNuevoTotal(Double nuevoTotal) { this.nuevoTotal = nuevoTotal; }

    public Double getTotalM2() { return totalM2; }
    public void setTotalM2(Double totalM2) { this.totalM2 = totalM2; }

    public Integer getLitrosPintura() { return litrosPintura; }
    public void setLitrosPintura(Integer litrosPintura) { this.litrosPintura = litrosPintura; }

    public Double getHorasEstimadas() { return horasEstimadas; }
    public void setHorasEstimadas(Double horasEstimadas) { this.horasEstimadas = horasEstimadas; }

    public Integer getNumPintores() { return numPintores; }
    public void setNumPintores(Integer numPintores) { this.numPintores = numPintores; }

    public Double getCosteMateriales() { return costeMateriales; }
    public void setCosteMateriales(Double costeMateriales) { this.costeMateriales = costeMateriales; }

    public Double getCosteManoObra() { return costeManoObra; }
    public void setCosteManoObra(Double costeManoObra) { this.costeManoObra = costeManoObra; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
