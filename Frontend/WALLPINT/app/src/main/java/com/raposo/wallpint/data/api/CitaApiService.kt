package com.raposo.wallpint.data.api

import com.raposo.wallpint.model.AsignarPintoresRequest
import com.raposo.wallpint.model.Cita
import com.raposo.wallpint.model.CrearCitaRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CitaApiService {

    @GET("api/citas/cliente/{clienteId}")
    suspend fun getCitasCliente(@Path("clienteId") clienteId: Long): List<Cita>

    // Pintor: citas asignadas a él
    @GET("api/citas/pintor/{pintorId}")
    suspend fun getCitasPintor(@Path("pintorId") pintorId: Long): List<Cita>

    // Admin: todas las citas (cualquier cliente)
    @GET("api/citas")
    suspend fun getTodasLasCitas(): List<Cita>

    @POST("api/citas")
    suspend fun crearCita(@Body req: CrearCitaRequest): Response<Cita>

    @DELETE("api/citas/{id}")
    suspend fun eliminarCita(@Path("id") id: Long): Response<Unit>

    // Admin: cambiar estado de la cita
    @PUT("api/citas/{id}/estado")
    suspend fun cambiarEstado(
        @Path("id") id: Long,
        @Query("nuevoEstado") nuevoEstado: String
    ): Response<Cita>

    // Admin: detalle completo (con pintores asignados)
    @GET("api/citas/{id}")
    suspend fun getCitaPorId(@Path("id") id: Long): Cita

    // Admin: asignar pintores
    @PUT("api/citas/{id}/pintores")
    suspend fun asignarPintores(
        @Path("id") id: Long,
        @Body req: AsignarPintoresRequest
    ): Response<Cita>
}
