package com.raposo.wallpint.ui.cita

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.raposo.wallpint.model.Presupuesto
import com.raposo.wallpint.ui.presupuesto.PresupuestoViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val AzulOscuro = Color(0xFF002B5B)
private val AzulMedio = Color(0xFF0066CC)
private val AzulClaro = Color(0xFFD9F0FA)
private val FondoApp = Color(0xFFF4F7FB)
private val GrisTexto = Color(0xFF6B7280)
private val Rojo = Color(0xFFB91C1C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisCitasScreen(
    clienteId: Long,
    viewModel: CitaViewModel,
    presupuestoViewModel: PresupuestoViewModel,
    onCalcularPresupuesto: () -> Unit,
    onSolicitarCita: (Long, String?) -> Unit,
    onReprogramar: (Long, String?) -> Unit,
    onAbrirDetalleCita: (Long) -> Unit
) {
    val citas by viewModel.citas.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()
    val presupuestos by presupuestoViewModel.presupuestos.collectAsState()

    LaunchedEffect(clienteId) {
        viewModel.cargarCitasCliente(clienteId)
        presupuestoViewModel.cargarPresupuestosCliente(clienteId)
    }

    var mostrarSelectorPresupuesto by remember { mutableStateOf(false) }

    val ahora = remember { LocalDateTime.now() }
    val (proximas, historial) = remember(citas) {
        citas.partition { c ->
            try { LocalDateTime.parse(c.fechaHora.take(19)).isAfter(ahora) } catch (_: Exception) { true }
        }
    }

    Scaffold(
        containerColor = FondoApp
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = cargando,
            onRefresh = { viewModel.cargarCitasCliente(clienteId) },
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Cabecera
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                    Text("Mis citas", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ResumenChip(
                            etiqueta = "${proximas.size} próxima${if (proximas.size != 1) "s" else ""}",
                            fondo = AzulClaro,
                            color = AzulOscuro
                        )
                        Spacer(Modifier.width(8.dp))
                        ResumenChip(
                            etiqueta = "${historial.size} en historial",
                            fondo = Color(0xFFE5E7EB),
                            color = GrisTexto
                        )
                    }
                }

                when {
                    error != null -> Card(
                        modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(error!!, color = Rojo, modifier = Modifier.padding(16.dp), fontSize = 13.sp)
                    }
                    citas.isEmpty() && !cargando -> EmptyCitas(
                        onSolicitarCita = { mostrarSelectorPresupuesto = true }
                    )
                    else -> LazyColumn(
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (proximas.isNotEmpty()) {
                            item { SeccionTitulo("PRÓXIMAS") }
                            items(proximas, key = { it.id ?: it.fechaHora }) { cita ->
                                CitaCard(
                                    cita = cita,
                                    historico = false,
                                    onClick = { cita.id?.let(onAbrirDetalleCita) }
                                )
                            }
                        }
                        if (historial.isNotEmpty()) {
                            item {
                                Spacer(Modifier.height(8.dp))
                                SeccionTitulo("HISTORIAL")
                            }
                            items(historial, key = { it.id ?: it.fechaHora }) { cita ->
                                CitaCard(
                                    cita = cita,
                                    historico = true,
                                    onClick = { cita.id?.let(onAbrirDetalleCita) }
                                )
                            }
                        }
                        // Hint para solicitar otra cita
                        item {
                            Spacer(Modifier.height(16.dp))
                            HintNuevaCita(onSolicitar = { mostrarSelectorPresupuesto = true })
                            Spacer(Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }

    // Sheet para elegir un presupuesto y reservar cita desde ahí
    if (mostrarSelectorPresupuesto) {
        // Cada presupuesto solo puede tener una cita activa (no cancelada).
        // Calculamos el set de IDs ocupados para deshabilitarlos visualmente.
        val presupuestosConCita = remember(citas) {
            citas.asSequence()
                .filter { !it.estado.equals("CANCELADA", ignoreCase = true) }
                .mapNotNull { it.presupuestoId }
                .toSet()
        }
        SeleccionarPresupuestoSheet(
            presupuestos = presupuestos,
            presupuestosConCita = presupuestosConCita,
            onDismiss = { mostrarSelectorPresupuesto = false },
            onElegir = { p ->
                mostrarSelectorPresupuesto = false
                onSolicitarCita(p.id ?: 0L, p.referencia)
            },
            onCalcularNuevo = {
                mostrarSelectorPresupuesto = false
                onCalcularPresupuesto()
            }
        )
    }

}

@Composable
private fun ResumenChip(etiqueta: String, fondo: Color, color: Color) {
    Surface(color = fondo, shape = RoundedCornerShape(20.dp)) {
        Text(
            etiqueta,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun SeccionTitulo(texto: String) {
    Text(
        texto,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = AzulMedio,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
    )
}

@Composable
private fun EmptyCitas(onSolicitarCita: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(AzulClaro),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.DateRange, null, tint = AzulOscuro) }
                Spacer(Modifier.height(12.dp))
                Text("Aún no tienes citas", fontWeight = FontWeight.Bold, color = AzulOscuro)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Solicita una cita asociándola a un presupuesto. Si todavía no tienes ninguno, podrás calcular uno en el siguiente paso.",
                    fontSize = 13.sp,
                    color = GrisTexto,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onSolicitarCita,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulMedio)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Solicitar cita", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun HintNuevaCita(onSolicitar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AzulClaro),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Info, null, tint = AzulOscuro, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("¿Necesitas otra cita?", fontWeight = FontWeight.Bold, color = AzulOscuro, fontSize = 13.sp)
                Text(
                    "Elige uno de tus presupuestos o calcula uno nuevo.",
                    fontSize = 12.sp,
                    color = GrisTexto
                )
            }
            TextButton(onClick = onSolicitar) {
                Text("Solicitar", color = AzulOscuro, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.KeyboardArrowRight, null, tint = AzulOscuro)
            }
        }
    }
}

private data class EstiloEstadoCita(val texto: String, val fondo: Color, val color: Color)

private fun estiloEstado(estado: String): EstiloEstadoCita = when (estado.uppercase()) {
    "PENDIENTE" -> EstiloEstadoCita("Pendiente", Color(0xFFFEF3C7), Color(0xFF92400E))
    "CONFIRMADA" -> EstiloEstadoCita("Confirmada", Color(0xFFDCFCE7), Color(0xFF166534))
    "EN_CURSO" -> EstiloEstadoCita("En curso", Color(0xFFE0E7FF), Color(0xFF3730A3))
    "COMPLETADA" -> EstiloEstadoCita("Completada", Color(0xFFE5E7EB), Color(0xFF374151))
    "CANCELADA" -> EstiloEstadoCita("Cancelada", Color(0xFFFEE2E2), Color(0xFFB91C1C))
    else -> EstiloEstadoCita(estado, Color(0xFFE5E7EB), Color(0xFF6B7280))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CitaCard(cita: Cita, historico: Boolean, onClick: () -> Unit) {
    val estilo = estiloEstado(cita.estado)
    val fechaFormateada = remember(cita.fechaHora) { formatearFecha(cita.fechaHora) }
    val opacidad = if (historico) 0.6f else 1f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (historico) 0.dp else 1.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(AzulClaro.copy(alpha = opacidad)),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.DateRange, null, tint = AzulOscuro.copy(alpha = opacidad), modifier = Modifier.size(20.dp)) }
                    Spacer(Modifier.width(12.dp))
                    Text(cita.franja, fontWeight = FontWeight.Bold, color = AzulOscuro.copy(alpha = opacidad))
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
            Text(fechaFormateada, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AzulOscuro.copy(alpha = opacidad))
            if (cita.presupuestoReferencia != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Presupuesto ${cita.presupuestoReferencia}",
                    fontSize = 12.sp,
                    color = GrisTexto.copy(alpha = opacidad)
                )
            }
            if (!cita.notasInternas.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(cita.notasInternas, fontSize = 13.sp, color = GrisTexto.copy(alpha = opacidad))
            }
        }
    }
}

private fun formatearFecha(iso: String): String {
    return try {
        val ldt = LocalDateTime.parse(iso.take(19))
        ldt.format(DateTimeFormatter.ofPattern("EEE d MMM · HH:mm", Locale("es", "ES")))
            .replaceFirstChar { it.uppercase() }
    } catch (_: Exception) {
        iso
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeleccionarPresupuestoSheet(
    presupuestos: List<Presupuesto>,
    presupuestosConCita: Set<Long>,
    onDismiss: () -> Unit,
    onElegir: (Presupuesto) -> Unit,
    onCalcularNuevo: () -> Unit
) {
    val disponibles = presupuestos.count { (it.id ?: 0L) !in presupuestosConCita }
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
            Text("Solicitar cita", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AzulOscuro)
            Text(
                when {
                    presupuestos.isEmpty() ->
                        "Necesitas un presupuesto antes de solicitar una cita."
                    disponibles == 0 ->
                        "Todos tus presupuestos ya tienen una cita activa. Cancela alguna o calcula uno nuevo."
                    else ->
                        "Elige el presupuesto al que asociarás la cita."
                },
                fontSize = 12.sp,
                color = GrisTexto,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            if (presupuestos.isNotEmpty()) {
                Column(
                    modifier = Modifier.heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    presupuestos.forEach { p ->
                        val tieneCita = (p.id ?: 0L) in presupuestosConCita
                        PresupuestoSheetItem(
                            p = p,
                            tieneCita = tieneCita,
                            onClick = { if (!tieneCita) onElegir(p) }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))
            }

            // Acción secundaria: crear uno nuevo
            OutlinedButton(
                onClick = onCalcularNuevo,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AzulMedio),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AzulMedio)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (presupuestos.isEmpty()) "Calcular mi primer presupuesto"
                    else "Calcular un presupuesto nuevo",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PresupuestoSheetItem(p: Presupuesto, tieneCita: Boolean, onClick: () -> Unit) {
    val tintAvatar = if (tieneCita) Color(0xFFE5E7EB) else AzulClaro
    val tintIcono = if (tieneCita) GrisTexto else AzulOscuro
    val colorTitulo = if (tieneCita) GrisTexto else AzulOscuro

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
        enabled = !tieneCita,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(tintAvatar),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (tieneCita) Icons.Default.Lock else Icons.Default.Edit,
                    null,
                    tint = tintIcono,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(p.referencia, fontWeight = FontWeight.Bold, color = colorTitulo, fontSize = 14.sp)
                val total = p.total?.let { "%.2f €".format(it) } ?: "—"
                val numEst = "${p.estancias.size} estancia${if (p.estancias.size != 1) "s" else ""}"
                if (tieneCita) {
                    Text("$total · $numEst", fontSize = 12.sp, color = GrisTexto)
                    Text("Ya tiene una cita activa", fontSize = 11.sp, color = Color(0xFF92400E), fontWeight = FontWeight.Bold)
                } else {
                    Text("$total · $numEst", fontSize = 12.sp, color = GrisTexto)
                }
            }
            Icon(
                Icons.Default.KeyboardArrowRight,
                null,
                tint = if (tieneCita) GrisTexto else AzulOscuro
            )
        }
    }
}
