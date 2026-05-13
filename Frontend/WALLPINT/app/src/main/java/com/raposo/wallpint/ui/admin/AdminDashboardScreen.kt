package com.raposo.wallpint.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raposo.wallpint.model.Cita
import com.raposo.wallpint.ui.cita.CitaViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val AzulOscuro = Color(0xFF002B5B)
private val AzulMedio = Color(0xFF0066CC)
private val AzulClaro = Color(0xFFD9F0FA)
private val FondoApp = Color(0xFFF4F7FB)
private val GrisTexto = Color(0xFF6B7280)
private val Verde = Color(0xFF10B981)
private val Rojo = Color(0xFFB91C1C)
private val Amarillo = Color(0xFFF59E0B)

private enum class TabAdmin(val label: String, val estados: List<String>) {
    PENDIENTES("Pendientes", listOf("PENDIENTE")),
    ACTIVAS("Activas", listOf("CONFIRMADA", "EN_CURSO")),
    HISTORIAL("Historial", listOf("COMPLETADA", "CANCELADA"))
}

private enum class SeccionAdmin { CITAS, CLIENTES, PINTORES }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    nombreUsuario: String,
    citaViewModel: CitaViewModel,
    gestionViewModel: GestionUsuariosViewModel,
    onLogout: () -> Unit,
    onAbrirDetalle: (Long) -> Unit = {},
    onAbrirCliente: (Long) -> Unit = {},
    onAbrirPintor: (Long) -> Unit = {}
) {
    var seccionActual by remember { mutableStateOf(SeccionAdmin.CITAS) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("PANEL ADMIN", fontSize = 10.sp, color = AzulMedio, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text(
                            when (seccionActual) {
                                SeccionAdmin.CITAS -> "Hola, $nombreUsuario"
                                SeccionAdmin.CLIENTES -> "Clientes"
                                SeccionAdmin.PINTORES -> "Pintores"
                            },
                            fontWeight = FontWeight.Bold, color = AzulOscuro
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión", tint = AzulOscuro)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FondoApp)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = seccionActual == SeccionAdmin.CITAS,
                    onClick = { seccionActual = SeccionAdmin.CITAS },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Citas") },
                    label = { Text("Citas") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AzulMedio,
                        selectedTextColor = AzulMedio,
                        indicatorColor = AzulClaro
                    )
                )
                NavigationBarItem(
                    selected = seccionActual == SeccionAdmin.CLIENTES,
                    onClick = { seccionActual = SeccionAdmin.CLIENTES },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Clientes") },
                    label = { Text("Clientes") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AzulMedio,
                        selectedTextColor = AzulMedio,
                        indicatorColor = AzulClaro
                    )
                )
                NavigationBarItem(
                    selected = seccionActual == SeccionAdmin.PINTORES,
                    onClick = { seccionActual = SeccionAdmin.PINTORES },
                    icon = { Icon(Icons.Default.Build, contentDescription = "Pintores") },
                    label = { Text("Pintores") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AzulMedio,
                        selectedTextColor = AzulMedio,
                        indicatorColor = AzulClaro
                    )
                )
            }
        },
        containerColor = FondoApp
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (seccionActual) {
                SeccionAdmin.CITAS -> SeccionCitas(citaViewModel, onAbrirDetalle)
                SeccionAdmin.CLIENTES -> AdminClientesScreen(gestionViewModel, onAbrirCliente = onAbrirCliente)
                SeccionAdmin.PINTORES -> AdminPintoresScreen(gestionViewModel, onAbrirPintor = onAbrirPintor)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeccionCitas(
    citaViewModel: CitaViewModel,
    onAbrirDetalle: (Long) -> Unit
) {
    val citas by citaViewModel.citas.collectAsState()
    val cargando by citaViewModel.cargando.collectAsState()
    val error by citaViewModel.error.collectAsState()

    var tabSeleccionado by remember { mutableStateOf(TabAdmin.PENDIENTES) }

    LaunchedEffect(Unit) {
        citaViewModel.cargarTodasLasCitas()
    }

    val pendientes = citas.count { it.estado.uppercase() == "PENDIENTE" }
    val activas = citas.count { it.estado.uppercase() in listOf("CONFIRMADA", "EN_CURSO") }
    val historial = citas.count { it.estado.uppercase() in listOf("COMPLETADA", "CANCELADA") }

    val filtradas = citas.filter { it.estado.uppercase() in tabSeleccionado.estados }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatChip("Pendientes", pendientes.toString(), Amarillo, Modifier.weight(1f))
            StatChip("Activas", activas.toString(), Verde, Modifier.weight(1f))
            StatChip("Historial", historial.toString(), GrisTexto, Modifier.weight(1f))
        }

        TabRow(
            selectedTabIndex = TabAdmin.entries.indexOf(tabSeleccionado),
            containerColor = FondoApp,
            contentColor = AzulOscuro
        ) {
            TabAdmin.entries.forEach { tab ->
                Tab(
                    selected = tab == tabSeleccionado,
                    onClick = { tabSeleccionado = tab },
                    text = { Text(tab.label, fontWeight = FontWeight.Bold) }
                )
            }
        }

        PullToRefreshBox(
            isRefreshing = cargando,
            onRefresh = { citaViewModel.cargarTodasLasCitas() },
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                error != null -> Card(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(error!!, color = Rojo, modifier = Modifier.padding(16.dp), fontSize = 13.sp)
                }
                filtradas.isEmpty() && !cargando -> EmptyTab(tabSeleccionado)
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtradas, key = { it.id ?: it.fechaHora }) { cita ->
                        CitaAdminCard(
                            cita = cita,
                            onClick = { cita.id?.let(onAbrirDetalle) }
                        )
                    }
                    item { Spacer(Modifier.height(40.dp)) }
                }
            }
        }
    }
}

@Composable
private fun StatChip(label: String, valor: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                Spacer(Modifier.width(6.dp))
                Text(label.uppercase(), fontSize = 10.sp, color = GrisTexto, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            }
            Text(valor, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
        }
    }
}

@Composable
private fun EmptyTab(tab: TabAdmin) {
    Box(modifier = Modifier.fillMaxSize().padding(48.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.DateRange, null, tint = Color.LightGray, modifier = Modifier.size(56.dp))
            Spacer(Modifier.height(12.dp))
            val msg = when (tab) {
                TabAdmin.PENDIENTES -> "No hay citas pendientes de aprobar"
                TabAdmin.ACTIVAS -> "No hay citas confirmadas o en curso"
                TabAdmin.HISTORIAL -> "El historial está vacío"
            }
            Text(msg, color = GrisTexto, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

private data class EstiloChipEstado(val texto: String, val fondo: Color, val color: Color)

private fun estiloChipEstado(estado: String): EstiloChipEstado = when (estado.uppercase()) {
    "PENDIENTE" -> EstiloChipEstado("Pendiente", Color(0xFFFEF3C7), Color(0xFF92400E))
    "CONFIRMADA" -> EstiloChipEstado("Confirmada", Color(0xFFDCFCE7), Color(0xFF166534))
    "EN_CURSO" -> EstiloChipEstado("En curso", Color(0xFFE0E7FF), Color(0xFF3730A3))
    "COMPLETADA" -> EstiloChipEstado("Completada", Color(0xFFE5E7EB), Color(0xFF374151))
    "CANCELADA" -> EstiloChipEstado("Cancelada", Color(0xFFFEE2E2), Color(0xFFB91C1C))
    else -> EstiloChipEstado(estado, Color(0xFFE5E7EB), GrisTexto)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CitaAdminCard(cita: Cita, onClick: () -> Unit) {
    val estilo = estiloChipEstado(cita.estado)
    val fechaFormateada = remember(cita.fechaHora) { formatearFechaListado(cita.fechaHora) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        cita.clienteNombre?.trim()?.ifBlank { "Cliente #${cita.clienteId ?: "?"}" }
                            ?: "Cliente #${cita.clienteId ?: "?"}",
                        fontWeight = FontWeight.Bold,
                        color = AzulOscuro,
                        fontSize = 15.sp
                    )
                    if (!cita.clienteTelefono.isNullOrBlank()) {
                        Text(cita.clienteTelefono, fontSize = 12.sp, color = GrisTexto)
                    }
                }
                Surface(color = estilo.fondo, shape = RoundedCornerShape(8.dp)) {
                    Text(
                        estilo.texto.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = estilo.color,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        letterSpacing = 0.5.sp
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, null, tint = AzulMedio, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("$fechaFormateada · ${cita.franja}", fontWeight = FontWeight.SemiBold, color = AzulOscuro, fontSize = 13.sp)
            }
            if (cita.presupuestoReferencia != null) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, null, tint = GrisTexto, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Presupuesto ${cita.presupuestoReferencia}" +
                                (cita.presupuestoTotal?.let { " · %.2f €".format(it) } ?: ""),
                        fontSize = 12.sp,
                        color = GrisTexto
                    )
                }
            }
            if (!cita.notasInternas.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Surface(color = AzulClaro, shape = RoundedCornerShape(8.dp)) {
                    Text(
                        cita.notasInternas,
                        fontSize = 12.sp,
                        color = AzulOscuro,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

private fun formatearFechaListado(iso: String): String {
    return try {
        val ldt = LocalDateTime.parse(iso.take(19))
        ldt.format(DateTimeFormatter.ofPattern("EEE d MMM · HH:mm", Locale.forLanguageTag("es-ES")))
            .replaceFirstChar { it.uppercase() }
    } catch (_: Exception) {
        iso
    }
}
