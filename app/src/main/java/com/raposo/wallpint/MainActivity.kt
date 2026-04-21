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

        // 1. AUTO-LOGIN: Comprobamos si ya hay un token guardado
        val tokenManager = TokenManager(this)
        // (Asegúrate de que tu TokenManager tiene una función llamada getToken() o similar)
        val tokenGuardado = tokenManager.getToken()

        val rolGuardado = tokenManager.getRol() ?: "CLIENTE"

        // Decidimos la ruta inicial.
        // Nota: Si en tu TokenManager también pudieras guardar el ROL, sería perfecto ponerlo aquí.
        // Por ahora, si hay token, lo mandamos al dashboard como CLIENTE por defecto.
        val rutaInicial = if (!tokenGuardado.isNullOrBlank()) {
            "dashboard/$rolGuardado"
        } else {
            "login"
        }

        setContent {
            val navController = rememberNavController()

            // 2. Usamos la rutaInicial que hemos calculado arriba
            NavHost(navController = navController, startDestination = rutaInicial) {

                composable("login") {
                    LoginScreen(
                        viewModel = authViewModel,
                        onNavigateToRegister = {
                            authViewModel.resetState()
                            navController.navigate("register")
                        },
                        onLoginSuccess = { rol ->
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

                composable("dashboard/{rol}") { backStackEntry ->
                    val rol = backStackEntry.arguments?.getString("rol") ?: "CLIENTE"

                    DashboardScreen(
                        rol = rol,
                        // 3. NUEVO: Lógica del botón de cerrar sesión
                        onLogout = {
                            // a) Borramos el token para que la próxima vez pida Login
                            tokenManager.clearToken() // Asegúrate de tener este método en TokenManager

                            // b) Reseteamos el estado del ViewModel
                            authViewModel.resetState()

                            // c) Viajamos al Login y destruimos TODO el historial para no poder volver atrás
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