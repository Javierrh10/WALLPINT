package com.raposo.wallpint.data.api

import com.raposo.wallpint.model.ClienteResumen
import retrofit2.http.GET

interface ClienteApiService {

    @GET("api/clientes")
    suspend fun getTodos(): List<ClienteResumen>
}
