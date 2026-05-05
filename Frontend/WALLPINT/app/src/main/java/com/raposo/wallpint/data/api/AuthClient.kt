package com.raposo.wallpint.data.api

import com.raposo.wallpint.data.preferences.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // La URL de tu backend en Render
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private var tokenManager: TokenManager? = null

    fun init(manager: TokenManager) {
        tokenManager = manager
    }

    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()

        tokenManager?.getToken()?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        chain.proceed(requestBuilder.build())
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor (authInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val presupuestoApi: PresupuestoApiService by lazy {
        retrofit.create(PresupuestoApiService::class.java)
    }

    val citaApi: CitaApiService by lazy {
        retrofit.create(CitaApiService::class.java)
    }

    val pintorApi: PintorApiService by lazy {
        retrofit.create(PintorApiService::class.java)
    }

    val clienteApi: ClienteApiService by lazy {
        retrofit.create(ClienteApiService::class.java)
    }
}