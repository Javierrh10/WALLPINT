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

    @POST("api/auth/registro/cliente")
    suspend fun registroCliente(@Body request: AuthModels.RegisterRequest): Response<AuthModels.AuthResponse>

    @POST("api/auth/registro/pintor")
    suspend fun registroPintor(@Body request: AuthModels.RegisterRequest): Response<AuthModels.AuthResponse>

    @GET("api/auth/me")
    suspend fun obtenerPerfil(
        @Header("Authorization") token: String
    ): Response<AuthModels.UserProfileResponse>
}