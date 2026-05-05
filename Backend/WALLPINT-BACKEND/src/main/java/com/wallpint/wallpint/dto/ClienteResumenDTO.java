package com.wallpint.wallpint.dto;

import com.wallpint.wallpint.model.Cliente;

import java.time.LocalDateTime;

/**
 * DTO ligero del cliente para que el admin vea el listado.
 * No incluye passwordHash ni datos sensibles internos.
 */
public class ClienteResumenDTO {

    private Long id;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private String direccion;
    private LocalDateTime fechaRegistro;

    public ClienteResumenDTO() {}

    public static ClienteResumenDTO fromEntity(Cliente c) {
        ClienteResumenDTO dto = new ClienteResumenDTO();
        dto.id = c.getId();
        dto.nombre = c.getNombre();
        dto.apellidos = c.getApellidos();
        dto.email = c.getEmail();
        dto.telefono = c.getTelefono();
        dto.direccion = c.getDireccion();
        dto.fechaRegistro = c.getFechaRegistro();
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
