package com.raposo.wallpint.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // Cambia esto por tu IP local cuando pruebes en el móvil
    // Si usas emulador: http://10.0.2.2:8080/
    // Si usas móvil físico: http://TU_IP_LOCAL:8080/
    private const val BASE_URL = "https://wallpint-backend.onrender.com/"

    // El salvavidas para cuando Render se duerme (60 segundos de paciencia)
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val authApi: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
}