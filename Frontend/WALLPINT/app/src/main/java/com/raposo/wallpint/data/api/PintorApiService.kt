package com.raposo.wallpint.data.api

import com.raposo.wallpint.model.PintorResumen
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PintorApiService {

    @GET("api/pintores")
    suspend fun getTodos(): List<PintorResumen>

    @GET("api/pintores/activos")
    suspend fun getActivos(): List<PintorResumen>

    @PUT("api/pintores/{id}/activo")
    suspend fun cambiarActivo(
        @Path("id") id: Long,
        @Query("activo") activo: Boolean
    ): Response<PintorResumen>
}
