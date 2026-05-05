package com.raposo.wallpint.ui.cita

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raposo.wallpint.model.Cita
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
fun DetalleCitaClienteScreen(
    citaId: Long,
    citaViewModel: CitaViewModel,
    presupuestoViewModel: PresupuestoViewModel,
    onBack: () -> Unit,
    onReprogramar: (Long, String?) -> Unit
) {
    val citas by citaViewModel.citas.collectAsState()
    val presupuesto by presupuestoViewModel.detalle.collectAsState()

    val cita = remember(citas, citaId) { citas.firstOrNull { it.id == citaId } }
    var mostrarConfirmarCancelar by remember { mutableStateOf(false) }

    LaunchedEffect(cita?.presupuestoId) {
        cita?.presupuestoId?.let { presupuestoViewModel.cargarDetalle(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de cita", fontWeight = FontWeight.Bold, color = AzulOscuro) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = AzulOscuro)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FondoApp)
            )
        },
        containerColor = FondoApp
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (cita == null) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Info, null, tint = GrisTexto, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("No se ha encontrado esta cita", color = GrisTexto)
                }
            } else {
                Contenido(
                    cita = cita,
                    presupuestoDetalle = presupuesto?.takeIf { it.id == cita.presupuestoId },
                    onReprogramar = {
                        cita.presupuestoId?.let { onReprogramar(it, cita.presupuestoReferencia) }
                    },
                    onCancelar = { mostrarConfirmarCancelar = true }
                )
            }
        }
    }

    if (mostrarConfirmarCancelar && cita != null) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmarCancelar = false },
            title = { Text("Cancelar cita", fontWeight = FontWeight.Bold) },
            text = { Text("¿Seguro que quieres cancelar esta cita? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    cita.id?.let { citaViewModel.eliminarCita(it) }
                    mostrarConfirmarCancelar = false
                    onBack()
                }) { Text("Cancelar cita", color = Rojo, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmarCancelar = false }) { Text("Volver") }
            }
        )
    }
}

@Composable
private fun Contenido(
    cita: Cita,
    presupuestoDetalle: com.raposo.wallpint.model.Presupuesto?,
    onReprogramar: () -> Unit,
    onCancelar: () -> Unit
) {
    val estilo = estiloEstadoCita(cita.estado)
    val fechaFormateada = remember(cita.fechaHora) { formatearFechaLarga(cita.fechaHora) }

    val ahora = remember { LocalDateTime.now() }
    val esFutura = try {
        LocalDateTime.parse(cita.fechaHora.take(19)).isAfter(ahora)
    } catch (_: Exception) { true }
    val estadoFinal = cita.estado.equals("CANCELADA", true) || cita.estado.equals("COMPLETADA", true)
    val puedeAccionar = esFutura && !estadoFinal

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        // Hero card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = AzulOscuro)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = estilo.fondo, shape = RoundedCornerShape(8.dp)) {
                        Text(
                            estilo.texto.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = estilo.color,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            letterSpacing = 0.5.sp
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(fechaFormateada, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Franja: ${cita.franja}", fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))
            }
        }

        Spacer(Modifier.height(20.dp))

        // Pintores asignados
        Seccion("PINTORES QUE VENDRÁN")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (cita.pintores.isEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, null, tint = GrisTexto)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Aún no se han asignado pintores a tu cita.",
                            fontSize = 13.sp,
                            color = GrisTexto
                        )
                    }
                } else {
                    cita.pintores.forEachIndexed { i, p ->
                        if (i > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(AzulClaro),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    p.nombre.firstOrNull()?.uppercase() ?: "?",
                                    fontWeight = FontWeight.Bold,
                                    color = AzulOscuro
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(p.nombreCompleto, fontWeight = FontWeight.Bold, color = AzulOscuro)
                                if (!p.telefono.isNullOrBlank()) {
                                    Text("Tel: ${p.telefono}", fontSize = 12.sp, color = GrisTexto)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Presupuesto asociado
        if (cita.presupuestoReferencia != null) {
            Seccion("PRESUPUESTO ASOCIADO")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(cita.presupuestoReferencia, fontWeight = FontWeight.Bold, color = AzulOscuro)
                    cita.presupuestoTotal?.let {
                        Text("Total estimado: %.2f €".format(it), fontSize = 13.sp, color = GrisTexto)
                    }
                    if (presupuestoDetalle != null) {
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            MiniStat("Superficie", "${presupuestoDetalle.totalM2 ?: 0.0} m²", Modifier.weight(1f))
                            MiniStat("Pintura", "${presupuestoDetalle.litrosPintura ?: 0} L", Modifier.weight(1f))
                            MiniStat("Horas", "${presupuestoDetalle.horasEstimadas ?: 0.0} h", Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }

        // Notas
        if (!cita.notasInternas.isNullOrBlank()) {
            Seccion("TUS NOTAS")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AzulClaro)
            ) {
                Text(
                    cita.notasInternas,
                    modifier = Modifier.padding(16.dp),
                    color = AzulOscuro,
                    fontSize = 13.sp
                )
            }
            Spacer(Modifier.height(20.dp))
        }

        // Acciones
        if (puedeAccionar) {
            Seccion("ACCIONES")
            if (cita.presupuestoId != null) {
                Button(
                    onClick = onReprogramar,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulMedio)
                ) {
                    Icon(Icons.Default.Refresh, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Reprogramar cita", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
            }
            OutlinedButton(
                onClick = onCancelar,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Rojo),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Rojo)
            ) {
                Icon(Icons.Default.Close, null)
                Spacer(Modifier.width(8.dp))
                Text("Cancelar cita", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun Seccion(texto: String) {
    Text(
        texto,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = AzulMedio,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun MiniStat(label: String, valor: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label.uppercase(), fontSize = 9.sp, color = GrisTexto, fontWeight = FontWeight.Bold)
        Text(valor, fontSize = 14.sp, color = AzulOscuro, fontWeight = FontWeight.Bold)
    }
}

private data class EstiloCita(val texto: String, val fondo: Color, val color: Color)

private fun estiloEstadoCita(estado: String): EstiloCita = when (estado.uppercase()) {
    "PENDIENTE" -> EstiloCita("Pendiente", Color(0xFFFEF3C7), Color(0xFF92400E))
    "CONFIRMADA" -> EstiloCita("Confirmada", Color(0xFFDCFCE7), Color(0xFF166534))
    "EN_CURSO" -> EstiloCita("En curso", Color(0xFFE0E7FF), Color(0xFF3730A3))
    "COMPLETADA" -> EstiloCita("Completada", Color(0xFFE5E7EB), Color(0xFF374151))
    "CANCELADA" -> EstiloCita("Cancelada", Color(0xFFFEE2E2), Color(0xFFB91C1C))
    else -> EstiloCita(estado, Color(0xFFE5E7EB), GrisTexto)
}

private fun formatearFechaLarga(iso: String): String {
    return try {
        val ldt = LocalDateTime.parse(iso.take(19))
        ldt.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM yyyy · HH:mm", Locale.forLanguageTag("es-ES")))
            .replaceFirstChar { it.uppercase() }
    } catch (_: Exception) {
        iso
    }
}
