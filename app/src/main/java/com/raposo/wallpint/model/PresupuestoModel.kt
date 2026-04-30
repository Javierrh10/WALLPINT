package com.raposo.wallpint.model

enum class EstadoPresupuesto {
    ORIENTATIVO,
    DEFINITIVO,
    PENDIENTE_ACEPTACION,
    ACEPTADO,
    RECHAZADO
}

enum class TipoPresupuesto {
    ORIENTATIVO,
    DEFINITIVO
}

enum class EstadoPared {
    NUEVO,
    IMPRIMACION,
    DETERIORADO
}

data class Estancia(
    val id: Long? = null,
    val nombre: String,
    val ancho: Double,
    val largo: Double,
    val alto: Double,
    val estadoParedes: EstadoPared,
    val numPuertas: Int = 0,
    val numVentanas: Int = 0,
    val color: String? = null,
    val numCapas: Int = 1,
    val incluirTecho: Boolean = false
)

data class Presupuesto(
    val id: Long? = null,
    val referencia: String,
    val fechaSolicitud: String? = null,
    val tipo: TipoPresupuesto = TipoPresupuesto.ORIENTATIVO,
    val estado: EstadoPresupuesto = EstadoPresupuesto.ORIENTATIVO,
    val totalM2: Double? = null,
    val litrosPintura: Int? = null,
    val horasEstimadas: Double? = null,
    val numPintores: Int? = null,
    val costeMateriales: Double? = null,
    val costeManoObra: Double? = null,
    val iva: Double? = null,
    val total: Double? = null,
    val estancias: List<Estancia> = emptyList()
)

data class EstanciaRequest(
    val nombre: String,
    val ancho: Double,
    val largo: Double,
    val alto: Double,
    val estadoParedes: EstadoPared,
    val numPuertas: Int = 0,
    val numVentanas: Int = 0,
    val color: String? = null,
    val numCapas: Int = 1,
    val incluirTecho: Boolean = false
)

data class CalcularPresupuestoRequest(
    val clienteId: Long,
    val estancias: List<EstanciaRequest>
)
