package com.raposo.wallpint.ui.presupuesto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raposo.wallpint.data.api.PresupuestoApiService
import com.raposo.wallpint.model.CalcularPresupuestoRequest
import com.raposo.wallpint.model.EstanciaRequest
import com.raposo.wallpint.model.Presupuesto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PresupuestoViewModel(
    private val apiService: PresupuestoApiService
) : ViewModel() {

    // ========== LISTADO ==========
    private val _presupuestos = MutableStateFlow<List<Presupuesto>>(emptyList())
    val presupuestos: StateFlow<List<Presupuesto>> = _presupuestos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargarPresupuestosCliente(clienteId: Long) {
        if (clienteId <= 0L) {
            _error.value = "Sesión inválida. Vuelve a iniciar sesión."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _presupuestos.value = apiService.getPresupuestosCliente(clienteId)
            } catch (e: Exception) {
                _error.value = "No se pudieron cargar los presupuestos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ========== CREACIÓN (FLUJO MULTI-STEP) ==========
    private val _datosGenerales = MutableStateFlow(DatosGeneralesProyecto())
    val datosGenerales: StateFlow<DatosGeneralesProyecto> = _datosGenerales.asStateFlow()

    private val _estanciasNuevas = MutableStateFlow<List<EstanciaRequest>>(emptyList())
    val estanciasNuevas: StateFlow<List<EstanciaRequest>> = _estanciasNuevas.asStateFlow()

    private val _resultado = MutableStateFlow<Presupuesto?>(null)
    val resultado: StateFlow<Presupuesto?> = _resultado.asStateFlow()

    private val _calculando = MutableStateFlow(false)
    val calculando: StateFlow<Boolean> = _calculando.asStateFlow()

    private val _errorCalculo = MutableStateFlow<String?>(null)
    val errorCalculo: StateFlow<String?> = _errorCalculo.asStateFlow()

    fun setDatosGenerales(nombreProyecto: String, direccion: String) {
        _datosGenerales.value = DatosGeneralesProyecto(nombreProyecto, direccion)
    }

    fun addEstancia(estancia: EstanciaRequest) {
        _estanciasNuevas.value = _estanciasNuevas.value + estancia
    }

    fun removeEstancia(index: Int) {
        _estanciasNuevas.value = _estanciasNuevas.value.toMutableList().apply {
            if (index in indices) removeAt(index)
        }
    }

    fun resetFlujo() {
        _datosGenerales.value = DatosGeneralesProyecto()
        _estanciasNuevas.value = emptyList()
        _resultado.value = null
        _errorCalculo.value = null
    }

    fun calcular(clienteId: Long, onSuccess: () -> Unit) {
        if (clienteId <= 0L) {
            _errorCalculo.value = "Sesión inválida."
            return
        }
        if (_estanciasNuevas.value.isEmpty()) {
            _errorCalculo.value = "Debes añadir al menos una estancia."
            return
        }
        viewModelScope.launch {
            _calculando.value = true
            _errorCalculo.value = null
            try {
                val request = CalcularPresupuestoRequest(
                    clienteId = clienteId,
                    estancias = _estanciasNuevas.value
                )
                val response = apiService.calcularPresupuesto(request)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    _resultado.value = body
                    onSuccess()
                } else {
                    _errorCalculo.value = "Error ${response.code()}: ${response.errorBody()?.string() ?: "respuesta vacía"}"
                }
            } catch (e: Exception) {
                _errorCalculo.value = "Error de conexión: ${e.message}"
            } finally {
                _calculando.value = false
            }
        }
    }
}

data class DatosGeneralesProyecto(
    val nombreProyecto: String = "",
    val direccion: String = ""
)
