package com.raposo.wallpint.ui.presupuesto

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raposo.wallpint.data.api.PresupuestoApiService
import com.raposo.wallpint.model.CalcularPresupuestoRequest
import com.raposo.wallpint.model.EstanciaRequest
import com.raposo.wallpint.model.MarcarDefinitivoRequest
import com.raposo.wallpint.model.Presupuesto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

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

    // ========== DETALLE ==========
    private val _detalle = MutableStateFlow<Presupuesto?>(null)
    val detalle: StateFlow<Presupuesto?> = _detalle.asStateFlow()

    private val _cargandoDetalle = MutableStateFlow(false)
    val cargandoDetalle: StateFlow<Boolean> = _cargandoDetalle.asStateFlow()

    private val _errorDetalle = MutableStateFlow<String?>(null)
    val errorDetalle: StateFlow<String?> = _errorDetalle.asStateFlow()

    private val _eliminando = MutableStateFlow(false)
    val eliminando: StateFlow<Boolean> = _eliminando.asStateFlow()

    private val _descargandoPdf = MutableStateFlow(false)
    val descargandoPdf: StateFlow<Boolean> = _descargandoPdf.asStateFlow()

    private val _errorPdf = MutableStateFlow<String?>(null)
    val errorPdf: StateFlow<String?> = _errorPdf.asStateFlow()

    private val _accionEnCurso = MutableStateFlow(false)
    val accionEnCurso: StateFlow<Boolean> = _accionEnCurso.asStateFlow()

    /**
     * Admin/pintor: convierte el presupuesto en DEFINITIVO ajustando los datos
     * tras la visita técnica (m², litros, horas, costes…). Cualquier campo
     * que sea null se mantiene como está en el presupuesto orientativo.
     */
    fun marcarDefinitivo(id: Long, request: MarcarDefinitivoRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _accionEnCurso.value = true
            _errorDetalle.value = null
            try {
                val response = apiService.marcarDefinitivo(id, request)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    _detalle.value = body
                    _presupuestos.value = _presupuestos.value.map { if (it.id == id) body else it }
                    onSuccess()
                } else {
                    _errorDetalle.value = "Error ${response.code()}: ${response.errorBody()?.string() ?: "respuesta vacía"}"
                }
            } catch (e: Exception) {
                _errorDetalle.value = "Error de conexión: ${e.message}"
            } finally {
                _accionEnCurso.value = false
            }
        }
    }

    /** Cliente: aceptar/rechazar el definitivo. */
    fun responder(id: Long, aceptar: Boolean, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _accionEnCurso.value = true
            _errorDetalle.value = null
            try {
                val response = apiService.responder(id, aceptar)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    _detalle.value = body
                    _presupuestos.value = _presupuestos.value.map { if (it.id == id) body else it }
                    onSuccess()
                } else {
                    _errorDetalle.value = "Error ${response.code()}: ${response.errorBody()?.string() ?: "respuesta vacía"}"
                }
            } catch (e: Exception) {
                _errorDetalle.value = "Error de conexión: ${e.message}"
            } finally {
                _accionEnCurso.value = false
            }
        }
    }

    /**
     * Descarga el PDF del presupuesto, lo guarda en cache y lo abre con un visor.
     * El FileProvider declarado en el manifest expone el archivo a apps externas.
     */
    fun descargarYAbrirPdf(id: Long, referencia: String, context: Context) {
        viewModelScope.launch {
            _descargandoPdf.value = true
            _errorPdf.value = null
            try {
                val response = apiService.descargarPdf(id)
                val body = response.body()
                if (!response.isSuccessful || body == null) {
                    _errorPdf.value = "Error ${response.code()} al descargar el PDF"
                    return@launch
                }

                val file = withContext(Dispatchers.IO) {
                    val dir = File(context.cacheDir, "pdfs").apply { mkdirs() }
                    val nombre = "presupuesto-${referencia.replace(Regex("[^A-Za-z0-9_-]"), "_")}.pdf"
                    val destino = File(dir, nombre)
                    body.byteStream().use { input ->
                        destino.outputStream().use { output -> input.copyTo(output) }
                    }
                    destino
                }

                val uri: Uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                try {
                    context.startActivity(intent)
                } catch (_: android.content.ActivityNotFoundException) {
                    _errorPdf.value = "No hay un visor de PDF instalado en este dispositivo."
                }
            } catch (e: Exception) {
                _errorPdf.value = "No se pudo descargar el PDF: ${e.message}"
            } finally {
                _descargandoPdf.value = false
            }
        }
    }

    fun cargarDetalle(id: Long) {
        viewModelScope.launch {
            _cargandoDetalle.value = true
            _errorDetalle.value = null
            _detalle.value = null
            try {
                _detalle.value = apiService.getPresupuestoById(id)
            } catch (e: Exception) {
                _errorDetalle.value = "No se pudo cargar el presupuesto: ${e.message}"
            } finally {
                _cargandoDetalle.value = false
            }
        }
    }

    fun eliminarPresupuesto(id: Long, clienteId: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            _eliminando.value = true
            try {
                val response = apiService.eliminarPresupuesto(id)
                if (response.isSuccessful) {
                    // Quitamos el eliminado de la lista local sin esperar al servidor
                    _presupuestos.value = _presupuestos.value.filter { it.id != id }
                    onDone()
                } else {
                    _errorDetalle.value = "No se pudo eliminar (${response.code()})"
                }
            } catch (e: Exception) {
                _errorDetalle.value = "Error al eliminar: ${e.message}"
            } finally {
                _eliminando.value = false
            }
        }
    }

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

    fun editarEstancia(index: Int, nueva: EstanciaRequest) {
        _estanciasNuevas.value = _estanciasNuevas.value.toMutableList().apply {
            if (index in indices) set(index, nueva)
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
