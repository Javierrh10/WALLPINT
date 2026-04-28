package com.raposo.wallpint.ui.presupuesto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raposo.wallpint.model.Presupuesto
import com.raposo.wallpint.data.api.PresupuestoApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PresupuestoViewModel(
    private val apiService: PresupuestoApiService
) : ViewModel() {

    // 1. Aquí guardamos la lista de presupuestos que nos manda Render
    private val _presupuestos = MutableStateFlow<List<Presupuesto>>(emptyList())
    val presupuestos: StateFlow<List<Presupuesto>> = _presupuestos

    // 2. Para mostrar un "Cargando..." en la pantalla
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 3. Para atrapar errores (ej: si no hay internet)
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // ============== MÉTODOS PARA PEDIR DATOS ==============

    fun cargarPresupuestosCliente(clienteId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null // Limpiamos errores anteriores

            try {
                // Llamamos a Render y esperamos la respuesta
                val lista = apiService.getPresupuestosCliente(clienteId)
                _presupuestos.value = lista
            } catch (e: Exception) {
                // Si algo falla, guardamos el error
                _error.value = "Error al conectar con el servidor: ${e.message}"
            } finally {
                // Pase lo que pase, quitamos el modo "Cargando"
                _isLoading.value = false
            }
        }
    }

    fun cargarPresupuestosPintor(pintorId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val lista = apiService.getPresupuestosPintor(pintorId)
                _presupuestos.value = lista
            } catch (e: Exception) {
                _error.value = "Error al conectar con el servidor: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}