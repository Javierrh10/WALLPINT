package com.raposo.wallpint.model

enum class FranjaCita(val etiqueta: String) {
    MANANA("Mañana"),
    TARDE("Tarde")
}

data class Cita(
    val id: Long? = null,
    val fechaHora: String,             // ISO-8601: "2026-05-12T09:00:00"
    val franja: String,                // "Mañana" o "Tarde"
    val estado: String,                // "PENDIENTE", "CONFIRMADA", "CANCELADA", "COMPLETADA"
    val notasInternas: String? = null,
    val clienteId: Long? = null,
    val clienteNombre: String? = null,     // Visible solo para admin
    val clienteTelefono: String? = null,   // Visible solo para admin
    val presupuestoId: Long? = null,
    val presupuestoReferencia: String? = null,
    val presupuestoTotal: Double? = null,
    val pintores: List<PintorResumen> = emptyList()
)

data class CrearCitaRequest(
    val clienteId: Long,
    val presupuestoId: Long,           // Obligatorio: toda cita debe nacer de un presupuesto
    val fechaHora: String,             // ISO-8601 sin zona, ej "2026-05-12T09:00:00"
    val franja: String,
    val notas: String? = null
)
