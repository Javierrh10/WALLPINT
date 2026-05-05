package com.wallpint.wallpint.dto;

/**
 * Esta clase representa la solicitud de inicio de sesión que el cliente envía al servidor para autenticarse.
 *
 * @author : Javier Raposo Huelva
 * @version : 2026:04
 */
public class LoginRequest {
    private String email;
    private String password;

    // =============== Constructores ===============


    // =============== Getters y Setters ===============
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
