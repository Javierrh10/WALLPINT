package com.raposo.wallpint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.raposo.wallpint.data.preferences.TokenManager
import com.raposo.wallpint.ui.auth.AuthViewModel
import com.raposo.wallpint.ui.auth.LoginScreen
import com.raposo.wallpint.ui.auth.RegisterScreen
import com.raposo.wallpint.ui.dashboard.DashboardScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raposo.wallpint.data.api.ApiClient
import com.raposo.wallpint.ui.presupuesto.PresupuestoViewModel

class MainActivity : ComponentActivity() {

    private val authViewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val tokenManager = TokenManager(this@MainActivity)
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(tokenManager) as T
        }
    }

    private val authViewModel: AuthViewModel by viewModels { authViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager = TokenManager(this)

        ApiClient.init(tokenManager)

        val tokenGuardado = tokenManager.getToken()
        val rolGuardado = tokenManager.getRol() ?: "CLIENTE"

        // ¡RUTA LIMPIA! Quitamos el nombre de aquí para evitar el bug de Android.
        val rutaInicial = if (!tokenGuardado.isNullOrBlank()) {
            "dashboard/$rolGuardado"
        } else {
            "login"
        }

        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = rutaInicial) {

                composable("login") {
                    LoginScreen(
                        viewModel = authViewModel,
                        onNavigateToRegister = {
                            authViewModel.resetState()
                            navController.navigate("register")
                        },
                        // Recibimos rol y nombre, pero SOLO mandamos el rol a la ruta
                        onLoginSuccess = { rol, _ ->
                            navController.navigate("dashboard/$rol") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                composable("register") {
                    RegisterScreen(
                        viewModel = authViewModel,
                        onNavigateBackToLogin = {
                            navController.popBackStack()
                        }
                    )
                }

                // ¡RUTA LIMPIA! Solo esperamos el rol.
                composable("dashboard/{rol}") { backStackEntry ->
                    val rol = backStackEntry.arguments?.getString("rol") ?: "CLIENTE"

                    // Así nos saltamos los bugs de navegación de Jetpack Compose.
                    val nombreReal = tokenManager.getNombre() ?: "Usuario"

                    val presupuestoViewModel: PresupuestoViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return PresupuestoViewModel(ApiClient.presupuestoApi) as T
                            }
                        }
                    )

                    val clienteIdPrueba = 1L

                    DashboardScreen(
                        rol = rol,
                        nombreUsuario = nombreReal,
                        clienteId = clienteIdPrueba,
                        presupuestoViewModel = presupuestoViewModel,
                        onLogout = {
                            tokenManager.clearToken()
                            authViewModel.resetState()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}