package com.raposo.wallpint.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.raposo.wallpint.model.Presupuesto
import com.raposo.wallpint.ui.presupuesto.PresupuestoViewModel

private val AzulMedio = Color(0xFF0066CC)
private val FondoApp = Color(0xFFF4F7FB)

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    rol: String,
    nombreUsuario: String,
    clienteId: Long,
    presupuestoViewModel: PresupuestoViewModel,
    onLogout: () -> Unit,
    onNuevoPresupuesto: () -> Unit = {}
) {
    var selectedItem by remember { mutableIntStateOf(0) }

    val navItems = if (rol == "CLIENTE") {
        listOf(
            NavItem("Inicio", Icons.Default.Home, "inicio"),
            NavItem("Mis Citas", Icons.Default.DateRange, "citas"),
            NavItem("Perfil", Icons.Default.Person, "perfil")
        )
    } else {
        listOf(
            NavItem("Solicitudes", Icons.Default.Notifications, "solicitudes"),
            NavItem("Mis Trabajos", Icons.Default.Build, "trabajos"),
            NavItem("Perfil", Icons.Default.AccountCircle, "perfil")
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AzulMedio,
                            selectedTextColor = AzulMedio,
                            indicatorColor = AzulMedio.copy(alpha = 0.1f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (rol == "CLIENTE" && selectedItem == 0) {
                FloatingActionButton(
                    onClick = onNuevoPresupuesto,
                    containerColor = AzulMedio,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nuevo")
                }
            }
        },
        containerColor = FondoApp
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (rol) {
                "CLIENTE" -> {
                    when (selectedItem) {
                        0 -> VistaHomeCliente(
                            nombreUsuario = nombreUsuario,
                            clienteId = clienteId,
                            viewModel = presupuestoViewModel,
                        )
                        1 -> PantallaEnConstruccion("Tus citas y agenda aparecerán aquí.")
                        2 -> VistaPerfil(rol = rol, nombreUsuario = nombreUsuario, onLogout = onLogout)
                    }
                }
                else -> { // PINTOR
                    when (selectedItem) {
                        0 -> VistaPintor(nombreUsuario)
                        1 -> PantallaEnConstruccion("Aquí verás tus trabajos activos.")
                        2 -> VistaPerfil(rol = rol, nombreUsuario = nombreUsuario, onLogout = onLogout)
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaEnConstruccion(mensaje: String) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = mensaje, color = Color.Gray, textAlign = TextAlign.Center)
    }
}