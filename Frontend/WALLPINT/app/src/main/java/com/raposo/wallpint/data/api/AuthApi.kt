package com.raposo.wallpint.data.api

import com.raposo.wallpint.model.AuthModels
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: AuthModels.LoginRequest): Response<AuthModels.AuthResponse>

    // El backend devuelve texto plano ("Cliente registrado"/"Pintor registrado"),
    // por eso usamos Unit y no AuthResponse — Gson no debe intentar parsear nada.
    @POST("api/auth/registro/cliente")
    suspend fun registroCliente(@Body request: AuthModels.RegisterRequest): Response<Unit>

    @POST("api/auth/registro/pintor")
    suspend fun registroPintor(@Body request: AuthModels.RegisterRequest): Response<Unit>

    @GET("api/auth/me")
    suspend fun obtenerPerfil(
        @Header("Authorization") token: String
    ): Response<AuthModels.UserProfileResponse>

    @retrofit2.http.PUT("api/auth/me")
    suspend fun editarPerfil(
        @Body request: AuthModels.EditarPerfilRequest
    ): Response<AuthModels.UserProfileResponse>

    @retrofit2.http.PUT("api/auth/me/password")
    suspend fun cambiarPassword(
        @Body request: AuthModels.CambiarPasswordRequest
    ): Response<Unit>
}