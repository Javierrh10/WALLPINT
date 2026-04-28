package com.raposo.wallpint.model

enum class EstadoPresupuesto {
    PENDIENTE, ACEPTADO, RECHAZADO, FINALIZADO
}

data class Presupuesto(
    val id: Long?,
    val referencia: String,
    val descripcion: String,
    val total: Double?,
    val estado: EstadoPresupuesto,
    val fechaCreacion: String,
    val clienteId: Long,
    val nombreCliente: String?,
    val pintorId: Long?,
    val nombrePintor: String?
)