package com.raposo.wallpint.data.api

import com.raposo.wallpint.model.Presupuesto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.PUT

interface PresupuestoApiService {

    // Obtener los presupuestos de un cliente
    @GET("api/presupuestos/cliente/{clienteId}")
    suspend fun getPresupuestosCliente(
        @Path("clienteId") clienteId: Long
    ): List<Presupuesto>

    // Obtener los presupuestos de un pintor
    @GET("api/presupuestos/pintor/{pintorId}")
    suspend fun getPresupuestosPintor(
        @Path("pintorId") pintorId: Long
    ): List<Presupuesto>

    // Crear un nuevo presupuesto (cuando el cliente usa la calculadora)
    @POST("api/presupuestos")
    suspend fun crearPresupuesto(
        @Body presupuesto: Presupuesto
    ): Presupuesto

    // Responder a un presupuesto (el pintor pone el precio)
    @PUT("api/presupuestos/{id}/responder")
    suspend fun responderPresupuesto(
        @Path("id") id: Long,
        @Query("estado") estado: String,
        @Query("precio") precio: Double
    ): Presupuesto
}