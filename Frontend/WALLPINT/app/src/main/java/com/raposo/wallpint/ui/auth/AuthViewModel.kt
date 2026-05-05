package com.raposo.wallpint.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raposo.wallpint.data.api.ApiClient
import com.raposo.wallpint.model.AuthModels
import com.raposo.wallpint.data.preferences.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val rol: String, val nombreUsuario: String) : AuthState()
    object RegisterSuccess : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val tokenManager: TokenManager) : ViewModel() {

    var authState by mutableStateOf<AuthState>(AuthState.Idle)
        private set

    // ===== Perfil del usuario logueado =====
    private val _perfil = MutableStateFlow<AuthModels.UserProfileResponse?>(null)
    val perfil: StateFlow<AuthModels.UserProfileResponse?> = _perfil.asStateFlow()

    private val _cargandoPerfil = MutableStateFlow(false)
    val cargandoPerfil: StateFlow<Boolean> = _cargandoPerfil.asStateFlow()

    private val _errorPerfil = MutableStateFlow<String?>(null)
    val errorPerfil: StateFlow<String?> = _errorPerfil.asStateFlow()

    private val _accionPerfil = MutableStateFlow(false)
    val accionPerfil: StateFlow<Boolean> = _accionPerfil.asStateFlow()

    private val _mensajePerfil = MutableStateFlow<String?>(null)
    val mensajePerfil: StateFlow<String?> = _mensajePerfil.asStateFlow()

    /** Edita el perfil del usuario logueado (nombre/apellidos/teléfono). */
    fun editarPerfil(
        nombre: String,
        apellidos: String,
        telefono: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _accionPerfil.value = true
            _errorPerfil.value = null
            try {
                val response = ApiClient.authApi.editarPerfil(
                    AuthModels.EditarPerfilRequest(nombre, apellidos, telefono)
                )
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    _perfil.value = body
                    tokenManager.saveNombre(body.nombre)
                    _mensajePerfil.value = "Perfil actualizado"
                    onSuccess()
                } else {
                    _errorPerfil.value = traducirErrorPerfil(response.code(), response.errorBody()?.string())
                }
            } catch (e: Exception) {
                _errorPerfil.value = "Sin conexión: ${e.message}"
            } finally {
                _accionPerfil.value = false
            }
        }
    }

    /** Cambia la contraseña: requiere la actual para verificar identidad. */
    fun cambiarPassword(
        passwordActual: String,
        passwordNueva: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _accionPerfil.value = true
            _errorPerfil.value = null
            try {
                val response = ApiClient.authApi.cambiarPassword(
                    AuthModels.CambiarPasswordRequest(passwordActual, passwordNueva)
                )
                if (response.isSuccessful) {
                    _mensajePerfil.value = "Contraseña actualizada"
                    onSuccess()
                } else {
                    val body = response.errorBody()?.string().orEmpty()
                    _errorPerfil.value = if (body.contains("contraseña actual no es correcta", ignoreCase = true)) {
                        "La contraseña actual no es correcta."
                    } else {
                        traducirErrorPerfil(response.code(), body)
                    }
                }
            } catch (e: Exception) {
                _errorPerfil.value = "Sin conexión: ${e.message}"
            } finally {
                _accionPerfil.value = false
            }
        }
    }

    fun consumirMensajePerfil() { _mensajePerfil.value = null }
    fun consumirErrorPerfil() { _errorPerfil.value = null }

    private fun traducirErrorPerfil(codigo: Int, body: String?): String {
        val texto = body.orEmpty()
        return when {
            texto.contains("\"telefono\"", ignoreCase = true) ->
                "El teléfono debe tener exactamente 9 dígitos."
            texto.contains("\"passwordNueva\"", ignoreCase = true) ->
                "La nueva contraseña debe tener al menos 6 caracteres."
            codigo == 400 -> "Revisa los datos: alguno no es válido."
            codigo == 401 -> "Sesión expirada. Vuelve a iniciar sesión."
            codigo >= 500 -> "El servidor está fallando, inténtalo más tarde."
            else -> "No se pudo guardar (error $codigo)."
        }
    }

    /** Carga (o recarga) el perfil del usuario actualmente logueado. */
    fun cargarPerfil() {
        val token = tokenManager.getToken() ?: return
        viewModelScope.launch {
            _cargandoPerfil.value = true
            _errorPerfil.value = null
            try {
                val response = ApiClient.authApi.obtenerPerfil("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    _perfil.value = response.body()
                } else {
                    _errorPerfil.value = "No se pudo cargar el perfil (${response.code()})"
                }
            } catch (e: Exception) {
                _errorPerfil.value = "Sin conexión: ${e.message}"
            } finally {
                _cargandoPerfil.value = false
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val request = AuthModels.LoginRequest(email, pass)
                val response = ApiClient.authApi.login(request)
                val authResponse = response.body()

                if (response.isSuccessful && authResponse != null) {
                    tokenManager.saveToken(authResponse.token)
                    tokenManager.saveRol(authResponse.rol)
                    tokenManager.saveNombre(authResponse.nombre)
                    tokenManager.saveUserId(authResponse.id)

                    authState = AuthState.Success(authResponse.rol, authResponse.nombre)
                } else {
                    authState = AuthState.Error(traducirErrorLogin(response.code()))
                }
            } catch (e: Exception) {
                authState = AuthState.Error("Sin conexión con el servidor. Comprueba tu red e inténtalo otra vez.")
            }
        }
    }

    fun register(nombre: String, apellidos: String, email: String, telefono: String, pass: String, rol: String) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val request = AuthModels.RegisterRequest(nombre, apellidos, email, telefono, pass)

                val response = if (rol == "CLIENTE") {
                    ApiClient.authApi.registroCliente(request)
                } else {
                    ApiClient.authApi.registroPintor(request)
                }

                if (response.isSuccessful) {
                    authState = AuthState.RegisterSuccess
                } else {
                    authState = AuthState.Error(traducirErrorRegistro(response.code(), response.errorBody()?.string()))
                }
            } catch (e: Exception) {
                authState = AuthState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    /**
     * Traduce el cuerpo de error que devuelve el backend a un mensaje legible
     * para el usuario. Cubre los casos más comunes: validaciones, email duplicado.
     */
    private fun traducirErrorLogin(codigo: Int): String = when (codigo) {
        401, 403 -> "Email o contraseña incorrectos."
        404 -> "Esa cuenta no existe."
        429 -> "Demasiados intentos. Espera un momento."
        in 500..599 -> "El servidor está fallando, inténtalo más tarde."
        else -> "No se pudo iniciar sesión (error $codigo)."
    }

    private fun traducirErrorRegistro(codigo: Int, body: String?): String {
        val texto = body.orEmpty()
        return when {
            // Email ya existe → texto plano del controller
            texto.contains("No se ha podido completar el registro", ignoreCase = true) ->
                "Ese email ya está registrado. Prueba con otro o inicia sesión."
            // Errores de @Valid → JSON con campo "errors"
            texto.contains("\"email\"", ignoreCase = true) ->
                "El formato del email no es válido."
            texto.contains("\"telefono\"", ignoreCase = true) ->
                "El teléfono debe tener exactamente 9 dígitos."
            texto.contains("\"password\"", ignoreCase = true) ->
                "La contraseña debe tener al menos 6 caracteres."
            codigo == 400 -> "Revisa los datos: alguno no es válido."
            codigo == 401 -> "No autorizado."
            codigo >= 500 -> "El servidor está fallando, inténtalo más tarde."
            else -> "No se pudo completar el registro (error $codigo)."
        }
    }

    fun resetState() {
        authState = AuthState.Idle
    }
}