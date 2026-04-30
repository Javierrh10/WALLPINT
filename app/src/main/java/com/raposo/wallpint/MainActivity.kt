package com.raposo.wallpint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.raposo.wallpint.data.api.ApiClient
import com.raposo.wallpint.data.preferences.TokenManager
import com.raposo.wallpint.ui.auth.AuthViewModel
import com.raposo.wallpint.ui.auth.LoginScreen
import com.raposo.wallpint.ui.auth.RegisterScreen
import com.raposo.wallpint.ui.dashboard.DashboardScreen
import com.raposo.wallpint.ui.presupuesto.NuevoPresupuestoStep1Screen
import com.raposo.wallpint.ui.presupuesto.NuevoPresupuestoStep2Screen
import com.raposo.wallpint.ui.presupuesto.NuevoPresupuestoStep3Screen
import com.raposo.wallpint.ui.presupuesto.PresupuestoViewModel
import com.raposo.wallpint.ui.presupuesto.ResultadoPresupuestoScreen

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

        val rutaInicial = if (!tokenGuardado.isNullOrBlank()) "dashboard/$rolGuardado" else "login"

        setContent {
            val navController = rememberNavController()

            // ViewModel compartido entre dashboard y flujo de creación
            val presupuestoFactory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return PresupuestoViewModel(ApiClient.presupuestoApi) as T
                }
            }

            NavHost(navController = navController, startDestination = rutaInicial) {

                composable("login") {
                    LoginScreen(
                        viewModel = authViewModel,
                        onNavigateToRegister = {
                            authViewModel.resetState()
                            navController.navigate("register")
                        },
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
                        onNavigateBackToLogin = { navController.popBackStack() }
                    )
                }

                composable("dashboard/{rol}") { backStackEntry ->
                    val rol = backStackEntry.arguments?.getString("rol") ?: "CLIENTE"
                    val nombreReal = tokenManager.getNombre() ?: "Usuario"
                    val clienteId = tokenManager.getUserId()

                    val presupuestoViewModel: PresupuestoViewModel = viewModel(
                        viewModelStoreOwner = this@MainActivity,
                        factory = presupuestoFactory
                    )

                    DashboardScreen(
                        rol = rol,
                        nombreUsuario = nombreReal,
                        clienteId = clienteId,
                        presupuestoViewModel = presupuestoViewModel,
                        onLogout = {
                            tokenManager.clearToken()
                            authViewModel.resetState()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onNuevoPresupuesto = {
                            presupuestoViewModel.resetFlujo()
                            navController.navigate("presupuesto/step1")
                        }
                    )
                }

                composable("presupuesto/step1") {
                    val vm: PresupuestoViewModel = viewModel(
                        viewModelStoreOwner = this@MainActivity,
                        factory = presupuestoFactory
                    )
                    NuevoPresupuestoStep1Screen(
                        viewModel = vm,
                        onBack = { navController.popBackStack() },
                        onContinuar = { navController.navigate("presupuesto/step2") }
                    )
                }

                composable("presupuesto/step2") {
                    val vm: PresupuestoViewModel = viewModel(
                        viewModelStoreOwner = this@MainActivity,
                        factory = presupuestoFactory
                    )
                    NuevoPresupuestoStep2Screen(
                        viewModel = vm,
                        onBack = { navController.popBackStack() },
                        onContinuar = { navController.navigate("presupuesto/step3") }
                    )
                }

                composable("presupuesto/step3") {
                    val vm: PresupuestoViewModel = viewModel(
                        viewModelStoreOwner = this@MainActivity,
                        factory = presupuestoFactory
                    )
                    val clienteId = tokenManager.getUserId()
                    NuevoPresupuestoStep3Screen(
                        viewModel = vm,
                        clienteId = clienteId,
                        onBack = { navController.popBackStack() },
                        onCalculado = {
                            navController.navigate("presupuesto/resultado") {
                                popUpTo("dashboard/${tokenManager.getRol() ?: "CLIENTE"}") { inclusive = false }
                            }
                        }
                    )
                }

                composable("presupuesto/resultado") {
                    val vm: PresupuestoViewModel = viewModel(
                        viewModelStoreOwner = this@MainActivity,
                        factory = presupuestoFactory
                    )
                    ResultadoPresupuestoScreen(
                        viewModel = vm,
                        onCerrar = {
                            val rol = tokenManager.getRol() ?: "CLIENTE"
                            navController.navigate("dashboard/$rol") {
                                popUpTo("dashboard/$rol") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
