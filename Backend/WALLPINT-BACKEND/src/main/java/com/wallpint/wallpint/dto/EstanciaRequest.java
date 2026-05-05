package com.wallpint.wallpint.dto;

import com.wallpint.wallpint.model.EstadoPared;
import jakarta.validation.constraints.*;

public class EstanciaRequest {

    @NotBlank
    private String nombre;

    @NotNull @Positive
    private Double ancho;

    @NotNull @Positive
    private Double largo;

    @NotNull @Positive
    private Double alto;

    @NotNull
    private EstadoPared estadoParedes;

    @Min(0)
    private Integer numPuertas = 0;

    @Min(0)
    private Integer numVentanas = 0;

    private String color;

    @Min(1) @Max(2)
    private Integer numCapas = 1;

    private Boolean incluirTecho = false;

    // Getters y setters
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
}