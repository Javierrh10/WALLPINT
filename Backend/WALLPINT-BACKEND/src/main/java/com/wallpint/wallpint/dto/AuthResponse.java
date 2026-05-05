package com.wallpint.wallpint.dto;

/**
 * Esta clase representa la respuesta que se envía al cliente después de un intento de autenticación exitoso.
 * Contiene el token JWT generado que el cliente debe usar para autenticar futuras solicitudes a la API.
 *
 * @author : Javier Raposo Huelva
 * @version : 2026:04
 */
public class AuthResponse {

    private String token;
    private String rol;
    private String nombre;
    private Long id;

    // =============== Constructores ===============
    public AuthResponse(String token, String rol, String nombre, Long id) {
        this.token = token;
        this.rol = rol;
        this.nombre = nombre;
        this.id = id;
    }

    // =============== Getters y Setters ===============
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
