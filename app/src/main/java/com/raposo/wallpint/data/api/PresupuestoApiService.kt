package com.raposo.wallpint.data.api

import com.raposo.wallpint.model.CalcularPresupuestoRequest
import com.raposo.wallpint.model.Presupuesto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PresupuestoApiService {

    @GET("api/presupuestos/cliente/{clienteId}")
    suspend fun getPresupuestosCliente(
        @Path("clienteId") clienteId: Long
    ): List<Presupuesto>

    @GET("api/presupuestos/{id}")
    suspend fun getPresupuestoById(
        @Path("id") id: Long
    ): Presupuesto

    @POST("api/presupuestos/calcular")
    suspend fun calcularPresupuesto(
        @Body request: CalcularPresupuestoRequest
    ): Response<Presupuesto>

    @DELETE("api/presupuestos/{id}")
    suspend fun eliminarPresupuesto(
        @Path("id") id: Long
    ): Response<Unit>
}
