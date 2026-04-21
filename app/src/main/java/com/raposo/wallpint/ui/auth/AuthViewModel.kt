package com.raposo.wallpint.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raposo.wallpint.data.api.ApiClient
import com.raposo.wallpint.data.api.AuthModels
import com.raposo.wallpint.data.preferences.TokenManager
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val rol: String) : AuthState()
    object RegisterSuccess : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val tokenManager: TokenManager) : ViewModel() {

    // Este es el estado que "observará" nuestra pantalla de Compose
    var authState by mutableStateOf<AuthState>(AuthState.Idle)
        private set

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val request = AuthModels.LoginRequest(email, pass)
                val response = ApiClient.authApi.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!

                    // Guardamos el token para futuras peticiones
                    tokenManager.saveToken(authResponse.token)

                    // Guardamos el rol para futuras peticiones
                    tokenManager.saveRol(authResponse.rol)

                    // Avisamos a la UI que todo ha ido bien y pasamos el ROL
                    authState = AuthState.Success(authResponse.rol)
                } else {
                    authState = AuthState.Error("Email o contraseña incorrectos")
                }
            } catch (e: Exception) {
                authState = AuthState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun register(nombre: String, apellidos: String, email: String, telefono: String, pass: String, rol: String) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                // Pasamos los 5 datos a tu modelo
                val request = AuthModels.RegisterRequest(nombre, apellidos, email, telefono, pass)

                val response = if (rol == "CLIENTE") {
                    ApiClient.authApi.registroCliente(request)
                } else {
                    ApiClient.authApi.registroPintor(request)
                }

                if (response.isSuccessful) {
                    // Si todo va bien, avisamos a la pantalla
                    authState = AuthState.RegisterSuccess
                } else {
                    val codigoError = response.code() // Nos dirá si es 400, 403, 404...
                    val detalleError = response.errorBody()?.string() ?: "Sin detalles"

                    authState = AuthState.Error("Error $codigoError: $detalleError")
                }
            } catch (e: Exception) {
                authState = AuthState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    // Y esta pequeña función para limpiar la pantalla de errores si volvemos atrás
    fun resetState() {
        authState = AuthState.Idle
    }
}