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
    // ¡Aquí ya lo tenías bien preparado para pedir el nombre!
    data class Success(val rol: String, val nombreUsuario: String) : AuthState()
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
                // GOLPE 1: Hacemos Login para conseguir el Token
                val request = AuthModels.LoginRequest(email, pass)
                val response = ApiClient.authApi.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!

                    tokenManager.saveToken(authResponse.token)
                    tokenManager.saveRol(authResponse.rol)

                    val perfilResponse = ApiClient.authApi.obtenerPerfil("Bearer ${authResponse.token}")

                    if (perfilResponse.isSuccessful && perfilResponse.body() != null) {
                        val usuario = perfilResponse.body()!!

                        tokenManager.saveNombre(usuario.nombre)

                        authState = AuthState.Success(authResponse.rol, usuario.nombre)
                    } else {
                        // Si falla al pedir el perfil, mostramos un error
                        authState = AuthState.Error("Login correcto, pero error al cargar perfil")
                    }

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
                val request = AuthModels.RegisterRequest(nombre, apellidos, email, telefono, pass)

                val response = if (rol == "CLIENTE") {
                    ApiClient.authApi.registroCliente(request)
                } else {
                    ApiClient.authApi.registroPintor(request)
                }

                if (response.isSuccessful) {
                    authState = AuthState.RegisterSuccess
                } else {
                    val codigoError = response.code()
                    val detalleError = response.errorBody()?.string() ?: "Sin detalles"
                    authState = AuthState.Error("Error $codigoError: $detalleError")
                }
            } catch (e: Exception) {
                authState = AuthState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun resetState() {
        authState = AuthState.Idle
    }
}