package com.raposo.wallpint.data.api

class AuthModels {
    // Lo que enviamos al backend
    data class LoginRequest(
        val email: String,
        val password: String // Asegúrate de que se llame igual que en tu DTO de Spring Boot
    )

    // Lo que nos responde el backend
    data class AuthResponse(
        val token: String,
        val rol: String
    )

    // Lo que enviamos al backend para registrar un usuario
    data class RegisterRequest(
        val nombre: String,
        val apellidos: String,
        val email: String,
        val telefono: String,
        val password: String
    )

    // Lo que nos responde el backend después de registrar un usuario
    data class UserProfileResponse(
        val nombre: String,
        val apellidos: String,
        val email: String,
        val telefono: String,
        val password: String,
        val rol: String
    )
}