package com.raposo.wallpint.ui.cita

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raposo.wallpint.data.api.CitaApiService
import com.raposo.wallpint.data.api.PintorApiService
import com.raposo.wallpint.model.AsignarPintoresRequest
import com.raposo.wallpint.model.Cita
import com.raposo.wallpint.model.CrearCitaRequest
import com.raposo.wallpint.model.PintorResumen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CitaViewModel(
    private val apiService: CitaApiService,
    private val pintorApi: PintorApiService? = null
) : ViewModel() {

    private val _citas = MutableStateFlow<List<Cita>>(emptyList())
    val citas: StateFlow<List<Cita>> = _citas.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _creando = MutableStateFlow(false)
    val creando: StateFlow<Boolean> = _creando.asStateFlow()

    private val _errorCreacion = MutableStateFlow<String?>(null)
    val errorCreacion: StateFlow<String?> = _errorCreacion.asStateFlow()

    fun cargarCitasCliente(clienteId: Long) {
        if (clienteId <= 0L) {
            _error.value = "Sesión inválida."
            return
        }
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                _citas.value = apiService.getCitasCliente(clienteId)
            } catch (e: Exception) {
                _error.value = "No se pudieron cargar las citas: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun crearCita(
        clienteId: Long,
        presupuestoId: Long,
        fechaHoraIso: String,
        franja: String,
        notas: String?,
        onSuccess: () -> Unit
    ) {
        if (clienteId <= 0L) {
            _errorCreacion.value = "Sesión inválida."
            return
        }
        if (presupuestoId <= 0L) {
            _errorCreacion.value = "Falta el presupuesto asociado."
            return
        }
        viewModelScope.launch {
            _creando.value = true
            _errorCreacion.value = null
            try {
                val response = apiService.crearCita(
                    CrearCitaRequest(
                        clienteId = clienteId,
                        presupuestoId = presupuestoId,
                        fechaHora = fechaHoraIso,
                        franja = franja,
                        notas = notas
                    )
                )
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    _citas.value = listOf(body) + _citas.value
                    onSuccess()
                } else {
                    _errorCreacion.value = traducirErrorCita(response.code(), response.errorBody()?.string())
                }
            } catch (e: Exception) {
                _errorCreacion.value = "Error de conexión: ${e.message}"
            } finally {
                _creando.value = false
            }
        }
    }

    /** Admin: carga todas las citas del sistema. */
    fun cargarTodasLasCitas() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                _citas.value = apiService.getTodasLasCitas()
            } catch (e: Exception) {
                _error.value = "No se pudieron cargar las citas: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    /** Pintor: carga las citas asignadas a él. */
    fun cargarCitasPintor(pintorId: Long) {
        if (pintorId <= 0L) {
            _error.value = "Sesión inválida."
            return
        }
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                _citas.value = apiService.getCitasPintor(pintorId)
            } catch (e: Exception) {
                _error.value = "No se pudieron cargar tus trabajos: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    /** Admin: cambia el estado de una cita. Actualiza la lista local en el sitio. */
    fun cambiarEstado(id: Long, nuevoEstado: String) {
        viewModelScope.launch {
            try {
                val response = apiService.cambiarEstado(id, nuevoEstado)
                val actualizada = response.body()
                if (response.isSuccessful && actualizada != null) {
                    _citas.value = _citas.value.map {
                        if (it.id == id) actualizada else it
                    }
                } else {
                    _error.value = "No se pudo cambiar el estado (${response.code()})"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            }
        }
    }

    fun eliminarCita(id: Long) {
        viewModelScope.launch {
            try {
                val response = apiService.eliminarCita(id)
                if (response.isSuccessful) {
                    _citas.value = _citas.value.filter { it.id != id }
                }
            } catch (_: Exception) { /* silencioso, no romper UX */ }
        }
    }

    fun resetError() {
        _errorCreacion.value = null
    }

    private fun traducirErrorCita(codigo: Int, body: String?): String {
        val texto = body.orEmpty()
        return when {
            texto.contains("ya tiene una cita activa", ignoreCase = true) ->
                "Este presupuesto ya tiene una cita activa. Cancélala antes de crear otra."
            texto.contains("Ya tienes una cita", ignoreCase = true) || codigo == 409 ->
                "Ya tienes una cita reservada en esa fecha y franja."
            codigo == 400 -> "Revisa los datos de la cita."
            codigo == 401 || codigo == 403 -> "No tienes permiso para crear esta cita."
            codigo >= 500 -> "El servidor está fallando, inténtalo más tarde."
            else -> "No se pudo crear la cita (error $codigo)."
        }
    }

    // ============== ADMIN: detalle de cita + asignación de pintores ==============

    private val _citaDetalle = MutableStateFlow<Cita?>(null)
    val citaDetalle: StateFlow<Cita?> = _citaDetalle.asStateFlow()

    private val _cargandoDetalle = MutableStateFlow(false)
    val cargandoDetalle: StateFlow<Boolean> = _cargandoDetalle.asStateFlow()

    private val _pintoresDisponibles = MutableStateFlow<List<PintorResumen>>(emptyList())
    val pintoresDisponibles: StateFlow<List<PintorResumen>> = _pintoresDisponibles.asStateFlow()

    fun cargarDetalleCita(id: Long) {
        viewModelScope.launch {
            _cargandoDetalle.value = true
            try {
                _citaDetalle.value = apiService.getCitaPorId(id)
            } catch (e: Exception) {
                _error.value = "No se pudo cargar el detalle: ${e.message}"
            } finally {
                _cargandoDetalle.value = false
            }
        }
    }

    fun cargarPintoresDisponibles() {
        viewModelScope.launch {
            try {
                _pintoresDisponibles.value = pintorApi?.getActivos() ?: emptyList()
            } catch (e: Exception) {
                _error.value = "No se pudieron cargar los pintores: ${e.message}"
            }
        }
    }

    fun asignarPintores(citaId: Long, pintorIds: List<Long>) {
        viewModelScope.launch {
            try {
                val response = apiService.asignarPintores(citaId, AsignarPintoresRequest(pintorIds))
                val actualizada = response.body()
                if (response.isSuccessful && actualizada != null) {
                    _citaDetalle.value = actualizada
                    // Reflejar también en la lista
                    _citas.value = _citas.value.map { if (it.id == citaId) actualizada else it }
                } else {
                    _error.value = "No se pudo asignar pintores (${response.code()})"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            }
        }
    }

    /** Override del cambiarEstado existente para que también actualice el detalle si está abierto. */
    fun cambiarEstadoConDetalle(id: Long, nuevoEstado: String) {
        viewModelScope.launch {
            try {
                val response = apiService.cambiarEstado(id, nuevoEstado)
                val actualizada = response.body()
                if (response.isSuccessful && actualizada != null) {
                    _citas.value = _citas.value.map { if (it.id == id) actualizada else it }
                    if (_citaDetalle.value?.id == id) _citaDetalle.value = actualizada
                } else {
                    _error.value = "No se pudo cambiar el estado (${response.code()})"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            }
        }
    }
}
