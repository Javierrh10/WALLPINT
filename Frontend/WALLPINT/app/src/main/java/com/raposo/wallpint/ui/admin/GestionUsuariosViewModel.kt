package com.raposo.wallpint.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raposo.wallpint.data.api.ClienteApiService
import com.raposo.wallpint.data.api.PintorApiService
import com.raposo.wallpint.model.ClienteResumen
import com.raposo.wallpint.model.EditarClienteRequest
import com.raposo.wallpint.model.EditarPintorRequest
import com.raposo.wallpint.model.PintorResumen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel del panel admin para gestionar clientes y pintores.
 * Solo cubre operaciones de lectura y el toggle activo del pintor —
 * para TFG es suficiente y no toca eliminar usuarios (con todos sus
 * efectos colaterales en BD: presupuestos, citas, etc).
 */
class GestionUsuariosViewModel(
    private val clienteApi: ClienteApiService,
    private val pintorApi: PintorApiService
) : ViewModel() {

    private val _clientes = MutableStateFlow<List<ClienteResumen>>(emptyList())
    val clientes: StateFlow<List<ClienteResumen>> = _clientes.asStateFlow()

    private val _pintores = MutableStateFlow<List<PintorResumen>>(emptyList())
    val pintores: StateFlow<List<PintorResumen>> = _pintores.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargarClientes() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                _clientes.value = clienteApi.getTodos()
            } catch (e: Exception) {
                _error.value = "No se pudieron cargar los clientes: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun cargarPintores() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                _pintores.value = pintorApi.getTodos()
            } catch (e: Exception) {
                _error.value = "No se pudieron cargar los pintores: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    // ===== Detalle/edición cliente =====

    private val _clienteDetalle = MutableStateFlow<ClienteResumen?>(null)
    val clienteDetalle: StateFlow<ClienteResumen?> = _clienteDetalle.asStateFlow()

    private val _accionCliente = MutableStateFlow(false)
    val accionCliente: StateFlow<Boolean> = _accionCliente.asStateFlow()

    private val _errorCliente = MutableStateFlow<String?>(null)
    val errorCliente: StateFlow<String?> = _errorCliente.asStateFlow()

    fun cargarClientePorId(id: Long) {
        viewModelScope.launch {
            _accionCliente.value = true
            _errorCliente.value = null
            try {
                _clienteDetalle.value = clienteApi.getPorId(id)
            } catch (e: Exception) {
                _errorCliente.value = "No se pudo cargar el cliente: ${e.message}"
            } finally {
                _accionCliente.value = false
            }
        }
    }

    fun editarCliente(id: Long, request: EditarClienteRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _accionCliente.value = true
            _errorCliente.value = null
            try {
                val response = clienteApi.editar(id, request)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    _clienteDetalle.value = body
                    _clientes.value = _clientes.value.map { if (it.id == id) body else it }
                    onSuccess()
                } else {
                    _errorCliente.value = when (response.code()) {
                        409 -> "Ese email ya está en uso por otro cliente."
                        400 -> "Revisa los datos: alguno no es válido."
                        else -> "Error ${response.code()} al guardar."
                    }
                }
            } catch (e: Exception) {
                _errorCliente.value = "Error de conexión: ${e.message}"
            } finally {
                _accionCliente.value = false
            }
        }
    }

    fun consumirErrorCliente() { _errorCliente.value = null }

    // ===== Detalle/edición pintor =====

    private val _pintorDetalle = MutableStateFlow<PintorResumen?>(null)
    val pintorDetalle: StateFlow<PintorResumen?> = _pintorDetalle.asStateFlow()

    private val _accionPintor = MutableStateFlow(false)
    val accionPintor: StateFlow<Boolean> = _accionPintor.asStateFlow()

    private val _errorPintor = MutableStateFlow<String?>(null)
    val errorPintor: StateFlow<String?> = _errorPintor.asStateFlow()

    fun cargarPintorPorId(id: Long) {
        viewModelScope.launch {
            _accionPintor.value = true
            _errorPintor.value = null
            try {
                _pintorDetalle.value = pintorApi.getPorId(id)
            } catch (e: Exception) {
                _errorPintor.value = "No se pudo cargar el pintor: ${e.message}"
            } finally {
                _accionPintor.value = false
            }
        }
    }

    fun editarPintor(id: Long, request: EditarPintorRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _accionPintor.value = true
            _errorPintor.value = null
            try {
                val response = pintorApi.editar(id, request)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    _pintorDetalle.value = body
                    _pintores.value = _pintores.value.map { if (it.id == id) body else it }
                    onSuccess()
                } else {
                    _errorPintor.value = when (response.code()) {
                        409 -> "Ese email ya está en uso por otro pintor."
                        400 -> "Revisa los datos: alguno no es válido."
                        else -> "Error ${response.code()} al guardar."
                    }
                }
            } catch (e: Exception) {
                _errorPintor.value = "Error de conexión: ${e.message}"
            } finally {
                _accionPintor.value = false
            }
        }
    }

    /** Toggle activo desde el detalle: actualiza la lista y el detalle a la vez. */
    fun cambiarActivoConDetalle(pintorId: Long, activo: Boolean) {
        viewModelScope.launch {
            try {
                val response = pintorApi.cambiarActivo(pintorId, activo)
                val actualizado = response.body()
                if (response.isSuccessful && actualizado != null) {
                    _pintores.value = _pintores.value.map {
                        if (it.id == pintorId) actualizado else it
                    }
                    if (_pintorDetalle.value?.id == pintorId) {
                        _pintorDetalle.value = actualizado
                    }
                } else {
                    _errorPintor.value = "No se pudo cambiar el estado (${response.code()})"
                }
            } catch (e: Exception) {
                _errorPintor.value = "Error: ${e.message}"
            }
        }
    }

    fun consumirErrorPintor() { _errorPintor.value = null }

    /** Activa o desactiva un pintor. Refresca la lista local con la respuesta. */
    fun cambiarActivo(pintorId: Long, activo: Boolean) {
        viewModelScope.launch {
            try {
                val response = pintorApi.cambiarActivo(pintorId, activo)
                val actualizado = response.body()
                if (response.isSuccessful && actualizado != null) {
                    _pintores.value = _pintores.value.map {
                        if (it.id == pintorId) actualizado else it
                    }
                } else {
                    _error.value = "No se pudo cambiar el estado del pintor (${response.code()})"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            }
        }
    }
}
