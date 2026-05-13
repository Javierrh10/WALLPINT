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
import com.raposo.wallpint.ui.dashboard.CambiarPasswordScreen
import com.raposo.wallpint.ui.dashboard.DashboardScreen
import com.raposo.wallpint.ui.dashboard.EditarPerfilScreen
import com.raposo.wallpint.ui.admin.AdminDashboardScreen
import com.raposo.wallpint.ui.admin.DetalleCitaAdminScreen
import com.raposo.wallpint.ui.cita.CitaViewModel
import com.raposo.wallpint.ui.cita.DetalleCitaClienteScreen
import com.raposo.wallpint.ui.cita.SolicitarCitaScreen
import com.raposo.wallpint.ui.pintor.PintorDashboardScreen
import com.raposo.wallpint.ui.presupuesto.DetallePresupuestoScreen
import com.raposo.wallpint.ui.presupuesto.NuevoPresupuestoStep1Screen
import com.raposo.wallpint.ui.presupuesto.NuevoPresupuestoStep2Screen
import com.raposo.wallpint.ui.presupuesto.NuevoPresupuestoStep3Screen
import com.raposo.wallpint.ui.presupuesto.PresupuestoViewModel
import com.raposo.wallpint.ui.presupuesto.ResultadoPresupuestoScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

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

            val citaFactory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return CitaViewModel(ApiClient.citaApi, ApiClient.pintorApi) as T
                }
            }

            val gestionFactory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return com.raposo.wallpint.ui.admin.GestionUsuariosViewModel(
                        ApiClient.clienteApi, ApiClient.pintorApi
                    ) as T
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

                composable("perfil/editar") {
                    EditarPerfilScreen(
                        authViewModel = authViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("perfil/password") {
                    CambiarPasswordScreen(
                        authViewModel = authViewModel,
                        onBack = { navController.popBackStack() }
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
                    val citaViewModel: CitaViewModel = viewModel(
                        viewModelStoreOwner = this@MainActivity,
                        factory = citaFactory
                    )

                    // Si el usuario es ADMIN entramos al panel de administración
                    if (rol.equals("ADMIN", ignoreCase = true)) {
                        val gestionViewModel: com.raposo.wallpint.ui.admin.GestionUsuariosViewModel =
                            viewModel(
                                viewModelStoreOwner = this@MainActivity,
                                factory = gestionFactory
                            )
                        AdminDashboardScreen(
                            nombreUsuario = nombreReal,
                            citaViewModel = citaViewModel,
                            gestionViewModel = gestionViewModel,
                            onLogout = {
                                tokenManager.clearToken()
                                authViewModel.resetState()
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            onAbrirDetalle = { id ->
                                navController.navigate("admin/cita/$id")
                            },
                            onAbrirCliente = { id ->
                                navController.navigate("admin/cliente/$id")
                            },
                            onAbrirPintor = { id ->
                                navController.navigate("admin/pintor/$id")
                            }
                        )
                        return@composable
                    }

                    if (rol.equals("PINTOR", ignoreCase = true)) {
                        PintorDashboardScreen(
                            pintorId = clienteId,           // mismo getUserId(): el ID del usuario logueado
                            nombreUsuario = nombreReal,
                            citaViewModel = citaViewModel,
                            onLogout = {
                                tokenManager.clearToken()
                                authViewModel.resetState()
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            onAbrirDetalle = { id ->
                                navController.navigate("pintor/cita/$id")
                            }
                        )
                        return@composable
                    }

                    DashboardScreen(
                        rol = rol,
                        nombreUsuario = nombreReal,
                        clienteId = clienteId,
                        presupuestoViewModel = presupuestoViewModel,
                        citaViewModel = citaViewModel,
                        authViewModel = authViewModel,
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
                        },
                        onAbrirDetalle = { id ->
                            navController.navigate("presupuesto/detalle/$id")
                        },
                        onReprogramarCita = { presupuestoId, referencia ->
                            navController.navigate("cita/nueva?presupuestoId=$presupuestoId&referencia=${referencia ?: ""}")
                        },
                        onEditarPerfil = { navController.navigate("perfil/editar") },
                        onCambiarPassword = { navController.navigate("perfil/password") },
                        onAbrirDetalleCita = { id -> navController.navigate("cita/detalle/$id") }
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

                composable(
                    route = "presupuesto/detalle/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.LongType })
                ) { backStackEntry ->
                    val vm: PresupuestoViewModel = viewModel(
                        viewModelStoreOwner = this@MainActivity,
                        factory = presupuestoFactory
                    )
                    val cVm: CitaViewModel = viewModel(
                        viewModelStoreOwner = this@MainActivity,
                        factory = citaFactory
                    )
                    val id = backStackEntry.arguments?.getLong("id") ?: 0L
                    val clienteId = tokenManager.getUserId()
                    DetallePresupuestoScreen(
                        presupuestoId = id,
                        clienteId = clienteId,
                        viewModel = vm,
                        onBack = { navController.popBackStack() },
                        onEliminado = {
                            // Al borrar el presupuesto el backend también borra sus citas en
                            // cascada. Refrescamos la lista local de citas para que el cambio
                            // se vea inmediatamente en "Mis Citas" sin esperar al pull-to-refresh.
                            cVm.cargarCitasCliente(clienteId)
                            navController.popBackStack()
                        },
                        onSolicitarCita = { presupuestoId, referencia ->
                            navController.navigate("cita/nueva?presupuestoId=$presupuestoId&referencia=${referencia ?: ""}")
                        }
                    )
                }

                composable(
                    route = "cita/detalle/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.LongType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getLong("id") ?: 0L
                    val cVm: CitaViewModel = viewModel(
                        viewModelStoreOwner = this@MainActivity,
                        factory = citaFactory
                    )
                    val pVm: PresupuestoViewModel = viewModel(
                        viewModelStoreOwner = this@MainActivity,
                        factory = presupuestoFactory
                    )
                    DetalleCitaClienteScreen(
                        citaId = id,
                        citaViewModel = cVm,
                        presupuestoViewModel = pVm,
                        onBack = { navController.popBackStack() },
                        onReprogramar = { presupuestoId, referencia ->
                            // Igual que en MisCitas: borramos la actual y abrimos el calendario
                            cVm.eliminarCita(id)
                            navController.navigate("cita/nueva?presupuestoId=$presupuestoId&referencia=${referencia ?: ""}") {
                                popUpTo("cita/detalle/$id") { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    route = "cita/nueva?presupuestoId={presupuestoId}&referencia={referencia}",
                    arguments = listOf(
                        navArgument("presupuestoId") {
                            type = NavType.LongType
                            defaultValue = -1L
                        },
                        navArgument("referencia") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val vm: CitaViewModel = viewModel(
                        viewModelStoreOwner = this@MainActivity,
                        factory = citaFactory
                    )
                    val pVm: PresupuestoViewModel = viewModel(
                        viewModelStoreOwner = this@MainActivity,
                        factory = presupuestoFactory
                    )
                    val presupuestoIdArg = backStackEntry.arguments?.getLong("presupuestoId") ?: -1L
                    val referencia = backStackEntry.arguments?.getString("referencia")?.takeIf { it.isNotBlank() }
                    val clienteId = tokenManager.getUserId()
                    SolicitarCitaScreen(
                        clienteId = clienteId,
                        presupuestoId = presupuestoIdArg,
                        presupuestoReferencia = referencia,
                        viewModel = vm,
                        presupuestoViewModel = pVm,
                        onBack = { navController.popBackStack() },
                        onCreada = {
                            vm.cargarCitasCliente(clienteId)
                            navController.popBackStack()
                        }
                    )
                }

                composable(
                    route = "admin/cliente/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.LongType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getLong("id") ?: 0L
                    val gVm: com.raposo.wallpint.ui.admin.GestionUsuariosViewModel =
                        viewModel(viewModelStoreOwner = this@MainActivity, factory = gestionFactory)
                    val pVm: PresupuestoViewModel =
                        viewModel(viewModelStoreOwner = this@MainActivity, factory = presupuestoFactory)
                    val cVm: CitaViewModel =
                        viewModel(viewModelStoreOwner = this@MainActivity, factory = citaFactory)
                    com.raposo.wallpint.ui.admin.DetalleClienteAdminScreen(
                        clienteId = id,
                        gestionViewModel = gVm,
                        presupuestoViewModel = pVm,
                        citaViewModel = cVm,
                        onBack = { navController.popBackStack() },
                        onAbrirCita = { citaId -> navController.navigate("admin/cita/$citaId") },
                        onAbrirPresupuesto = { presupuestoId ->
                            navController.navigate("presupuesto/detalle/$presupuestoId")
                        }
                    )
                }

                composable(
                    route = "admin/pintor/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.LongType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getLong("id") ?: 0L
                    val gVm: com.raposo.wallpint.ui.admin.GestionUsuariosViewModel =
                        viewModel(viewModelStoreOwner = this@MainActivity, factory = gestionFactory)
                    val cVm: CitaViewModel =
                        viewModel(viewModelStoreOwner = this@MainActivity, factory = citaFactory)
                    com.raposo.wallpint.ui.admin.DetallePintorAdminScreen(
                        pintorId = id,
                        gestionViewModel = gVm,
                        citaViewModel = cVm,
                        onBack = { navController.popBackStack() },
                        onAbrirCita = { citaId -> navController.navigate("admin/cita/$citaId") }
                    )
                }

                composable(
                    route = "admin/cita/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.LongType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getLong("id") ?: 0L
                    val cVm: CitaViewModel = viewModel(
                        viewModelStoreOwner = this@MainActivity,
                        factory = citaFactory
                    )
                    val pVm: PresupuestoViewModel = viewModel(
                        viewModelStoreOwner = this@MainActivity,
                        factory = presupuestoFactory
                    )
                    DetalleCitaAdminScreen(
                        citaId = id,
                        citaViewModel = cVm,
                        presupuestoViewModel = pVm,
                        onBack = { navController.popBackStack() },
                        permitirAccionesAdmin = true
                    )
                }

                composable(
                    route = "pintor/cita/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.LongType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getLong("id") ?: 0L
                    val cVm: CitaViewModel = viewModel(
                        viewModelStoreOwner = this@MainActivity,
                        factory = citaFactory
                    )
                    val pVm: PresupuestoViewModel = viewModel(
                        viewModelStoreOwner = this@MainActivity,
                        factory = presupuestoFactory
                    )
                    DetalleCitaAdminScreen(
                        citaId = id,
                        citaViewModel = cVm,
                        presupuestoViewModel = pVm,
                        onBack = { navController.popBackStack() },
                        permitirAccionesAdmin = false
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
                            // Refrescamos la lista del cliente para que aparezca el nuevo presupuesto
                            vm.cargarPresupuestosCliente(tokenManager.getUserId())
                            val rol = tokenManager.getRol() ?: "CLIENTE"
                            navController.navigate("dashboard/$rol") {
                                popUpTo("dashboard/$rol") { inclusive = true }
                            }
                        },
                        onSolicitarCita = { presupuestoId, referencia ->
                            navController.navigate("cita/nueva?presupuestoId=$presupuestoId&referencia=${referencia ?: ""}") {
                                // Sacamos el resultado del back stack: tras crear la cita el usuario
                                // vuelve directo al dashboard, no a este resultado
                                popUpTo("dashboard/${tokenManager.getRol() ?: "CLIENTE"}") { inclusive = false }
                            }
                        }
                    )
                }
            }
        }
    }
}
