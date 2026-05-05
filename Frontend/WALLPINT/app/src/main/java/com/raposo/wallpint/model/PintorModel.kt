package com.raposo.wallpint.model

data class PintorResumen(
    val id: Long,
    val nombre: String,
    val apellidos: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val activo: Boolean? = null
) {
    val nombreCompleto: String
        get() = listOfNotNull(nombre, apellidos?.takeIf { it.isNotBlank() }).joinToString(" ")
}

data class AsignarPintoresRequest(
    val pintorIds: List<Long>
)

data class ClienteResumen(
    val id: Long,
    val nombre: String,
    val apellidos: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val direccion: String? = null,
    val fechaRegistro: String? = null
) {
    val nombreCompleto: String
        get() = listOfNotNull(nombre, apellidos?.takeIf { it.isNotBlank() }).joinToString(" ")
}
