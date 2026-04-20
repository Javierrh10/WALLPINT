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

// Estados posibles del Login para que la pantalla sepa qué mostrar
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val rol: String) : AuthState()
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
}