package com.raposo.wallpint.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: AuthModels.LoginRequest): Response<AuthModels.AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: AuthModels.RegisterRequest): Response<AuthModels.AuthResponse>
}