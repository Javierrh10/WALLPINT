package com.raposo.wallpint.data.api

import com.raposo.wallpint.data.preferences.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // La URL de tu backend en Render
    private const val BASE_URL = "https://wallpint-backend.onrender.com/"

    private var tokenManager: TokenManager? = null

    fun init(manager: TokenManager) {
        tokenManager = manager
    }

    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()

        val token = tokenManager?.getToken()

        android.util.Log.d("ApiClient", "Interceptor: Token obtenido: $token")

        // Si hay un token guardado, lo pegamos en la cabecera (Header) de la petición
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
}