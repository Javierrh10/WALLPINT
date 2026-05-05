package com.wallpint.wallpint.dto;

import com.wallpint.wallpint.model.Pintor;

/**
 * DTO ligero del pintor para listados y referencias en otros DTOs.
 * No incluye passwordHash ni datos sensibles.
 */
public class PintorResumenDTO {

    private Long id;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private Boolean activo;

    public PintorResumenDTO() {}

    public static PintorResumenDTO fromEntity(Pintor p) {
        PintorResumenDTO dto = new PintorResumenDTO();
        dto.id = p.getId();
        dto.nombre = p.getNombre();
        dto.apellidos = p.getApellidos();
        dto.email = p.getEmail();
        dto.telefono = p.getTelefono();
        dto.activo = p.getActivo();
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

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
