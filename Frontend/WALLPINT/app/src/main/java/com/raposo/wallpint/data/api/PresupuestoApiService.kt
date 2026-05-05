package com.raposo.wallpint.data.api

import com.raposo.wallpint.model.CalcularPresupuestoRequest
import com.raposo.wallpint.model.MarcarDefinitivoRequest
import com.raposo.wallpint.model.Presupuesto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

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

    @Streaming
    @GET("api/presupuestos/{id}/pdf")
    suspend fun descargarPdf(
        @Path("id") id: Long
    ): Response<ResponseBody>

    // Admin/pintor: convertir a DEFINITIVO con precio ajustado
    @PUT("api/presupuestos/{id}/definitivo")
    suspend fun marcarDefinitivo(
        @Path("id") id: Long,
        @Body req: MarcarDefinitivoRequest
    ): Response<Presupuesto>

    // Cliente: aceptar o rechazar el definitivo
    @PUT("api/presupuestos/{id}/responder")
    suspend fun responder(
        @Path("id") id: Long,
        @Query("aceptar") aceptar: Boolean
    ): Response<Presupuesto>
}
