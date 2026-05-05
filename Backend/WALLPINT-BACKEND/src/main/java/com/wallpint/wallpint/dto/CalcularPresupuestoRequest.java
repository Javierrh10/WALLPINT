package com.wallpint.wallpint.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CalcularPresupuestoRequest {

    @NotNull
    private Long clienteId;

    @NotEmpty
    @Valid
    private List<EstanciaRequest> estancias;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long c) { this.clienteId = c; }
    public List<EstanciaRequest> getEstancias() { return estancias; }
    public void setEstancias(List<EstanciaRequest> e) { this.estancias = e; }
}