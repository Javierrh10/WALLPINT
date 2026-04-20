package com.raposo.wallpint.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Cambia esto por tu IP local cuando pruebes en el móvil
    // Si usas emulador: http://10.0.2.2:8080/
    // Si usas móvil físico: http://TU_IP_LOCAL:8080/
    private const val BASE_URL = "https://wallpint-backend.onrender.com/"

    val authApi: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
}