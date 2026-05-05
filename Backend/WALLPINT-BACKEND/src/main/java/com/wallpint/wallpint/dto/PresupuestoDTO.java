package com.wallpint.wallpint.dto;

import com.wallpint.wallpint.model.EstadoPresupuesto;
import java.time.LocalDateTime;

/**
 * Esta clase ...
 *
 * @author : Javier Raposo Huelva
 * @version : 2026:04
 */
public class PresupuestoDTO {
    private Long id;
    private String referencia;
    private String descripcion;
    private Double total;
    private EstadoPresupuesto estado;
    private LocalDateTime fechaCreacion;

    // Solo enviamos los nombres o IDs para evitar bucles
    private Long clienteId;
    private String nombreCliente;
    private Long pintorId;
    private String nombrePintor;

    // Constructor vacío
    public PresupuestoDTO() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public EstadoPresupuesto getEstado() { return estado; }
    public void setEstado(EstadoPresupuesto estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public Long getPintorId() { return pintorId; }
    public void setPintorId(Long pintorId) { this.pintorId = pintorId; }

    public String getNombrePintor() { return nombrePintor; }
    public void setNombrePintor(String nombrePintor) { this.nombrePintor = nombrePintor; }
}
