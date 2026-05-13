package com.raposo.wallpint.data.api

import com.raposo.wallpint.model.ClienteResumen
import com.raposo.wallpint.model.EditarClienteRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ClienteApiService {

    @GET("api/clientes")
    suspend fun getTodos(): List<ClienteResumen>

    @GET("api/clientes/{id}")
    suspend fun getPorId(@Path("id") id: Long): ClienteResumen

    @PUT("api/clientes/{id}")
    suspend fun editar(
        @Path("id") id: Long,
        @Body req: EditarClienteRequest
    ): Response<ClienteResumen>
}
