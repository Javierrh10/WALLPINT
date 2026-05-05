package com.wallpint.wallpint.dto;

import com.wallpint.wallpint.model.Cita;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CitaDTO {

    private Long id;
    private LocalDateTime fechaHora;
    private String franja;
    private String estado;
    private String notasInternas;
    private Long clienteId;
    private String clienteNombre;
    private String clienteTelefono;
    private Long presupuestoId;
    private String presupuestoReferencia;
    private Double presupuestoTotal;
    private List<PintorResumenDTO> pintores = Collections.emptyList();

    public CitaDTO() {}

    public static CitaDTO fromEntity(Cita c) {
        CitaDTO dto = new CitaDTO();
        dto.id = c.getId();
        dto.fechaHora = c.getFechaHora();
        dto.franja = c.getFranja();
        dto.estado = c.getEstado();
        dto.notasInternas = c.getNotasInternas();
        if (c.getCliente() != null) {
            dto.clienteId = c.getCliente().getId();
            dto.clienteNombre = c.getCliente().getNombre() + " " +
                    (c.getCliente().getApellidos() != null ? c.getCliente().getApellidos() : "");
            dto.clienteTelefono = c.getCliente().getTelefono();
        }
        if (c.getPresupuesto() != null) {
            dto.presupuestoId = c.getPresupuesto().getId();
            dto.presupuestoReferencia = c.getPresupuesto().getReferencia();
            dto.presupuestoTotal = c.getPresupuesto().getTotal();
        }
        if (c.getPintores() != null) {
            dto.pintores = c.getPintores().stream()
                    .map(PintorResumenDTO::fromEntity)
                    .collect(Collectors.toList());
        }
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getFranja() { return franja; }
    public void setFranja(String franja) { this.franja = franja; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNotasInternas() { return notasInternas; }
    public void setNotasInternas(String notasInternas) { this.notasInternas = notasInternas; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getClienteTelefono() { return clienteTelefono; }
    public void setClienteTelefono(String clienteTelefono) { this.clienteTelefono = clienteTelefono; }

    public Long getPresupuestoId() { return presupuestoId; }
    public void setPresupuestoId(Long presupuestoId) { this.presupuestoId = presupuestoId; }

    public String getPresupuestoReferencia() { return presupuestoReferencia; }
    public void setPresupuestoReferencia(String presupuestoReferencia) { this.presupuestoReferencia = presupuestoReferencia; }

    public Double getPresupuestoTotal() { return presupuestoTotal; }
    public void setPresupuestoTotal(Double presupuestoTotal) { this.presupuestoTotal = presupuestoTotal; }

    public List<PintorResumenDTO> getPintores() { return pintores; }
    public void setPintores(List<PintorResumenDTO> pintores) { this.pintores = pintores; }
}
