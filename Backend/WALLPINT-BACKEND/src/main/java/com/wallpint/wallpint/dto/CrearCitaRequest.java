package com.wallpint.wallpint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class CrearCitaRequest {

    @NotNull
    private Long clienteId;

    @NotNull
    private Long presupuestoId;

    @NotNull
    private LocalDateTime fechaHora;

    @NotBlank
    private String franja;

    private String notas;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getPresupuestoId() { return presupuestoId; }
    public void setPresupuestoId(Long presupuestoId) { this.presupuestoId = presupuestoId; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getFranja() { return franja; }
    public void setFranja(String franja) { this.franja = franja; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
