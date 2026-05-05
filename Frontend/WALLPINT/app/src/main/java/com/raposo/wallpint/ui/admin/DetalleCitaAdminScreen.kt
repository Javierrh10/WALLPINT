package com.raposo.wallpint.ui.admin

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.raposo.wallpint.model.Cita
import com.raposo.wallpint.model.EstadoPresupuesto
import com.raposo.wallpint.model.PintorResumen
import com.raposo.wallpint.ui.cita.CitaViewModel
import com.raposo.wallpint.ui.presupuesto.PresupuestoViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCitaAdminScreen(
    citaId: Long,
    citaViewModel: CitaViewModel,
    presupuestoViewModel: PresupuestoViewModel,
    onBack: () -> Unit,
    // Si es false (rol PINTOR), se ocultan acciones de admin (asignar pintores,
    // confirmar/rechazar). El pintor solo puede marcar EN_CURSO/COMPLETADA/DEFINITIVO.
    permitirAccionesAdmin: Boolean = true
) {
    val cita by citaViewModel.citaDetalle.collectAsState()
    val cargando by citaViewModel.cargandoDetalle.collectAsState()
    val pintoresDisponibles by citaViewModel.pintoresDisponibles.collectAsState()
    val presupuesto by presupuestoViewModel.detalle.collectAsState()
    val accionEnCurso by presupuestoViewModel.accionEnCurso.collectAsState()
    var mostrarAsignar by remember { mutableStateOf(false) }
    var mostrarDefinitivo by remember { mutableStateOf(false) }

    LaunchedEffect(citaId) {
        citaViewModel.cargarDetalleCita(citaId)
    }
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
            when {
                cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AzulMedio)
                }
                cita != null -> ContenidoDetalle(
                    cita = cita!!,
                    presupuesto = presupuesto?.takeIf { it.id == cita!!.presupuestoId },
                    permitirAccionesAdmin = permitirAccionesAdmin,
                    onCambiarEstado = { citaViewModel.cambiarEstadoConDetalle(citaId, it) },
                    onAbrirAsignar = {
                        citaViewModel.cargarPintoresDisponibles()
                        mostrarAsignar = true
                    },
                    onAbrirDefinitivo = { mostrarDefinitivo = true }
                )
            }
        }
    }

    if (mostrarAsignar && cita != null) {
        AsignarPintoresSheet(
            seleccionadosIniciales = cita!!.pintores.mapNotNull { it.id },
            disponibles = pintoresDisponibles,
            onDismiss = { mostrarAsignar = false },
            onGuardar = { ids ->
                citaViewModel.asignarPintores(citaId, ids)
                mostrarAsignar = false
            }
        )
    }

    if (mostrarDefinitivo && presupuesto != null) {
        DialogMarcarDefinitivo(
            presupuestoActual = presupuesto!!,
            cargando = accionEnCurso,
            onDismiss = { mostrarDefinitivo = false },
            onConfirmar = { request ->
                presupuestoViewModel.marcarDefinitivo(presupuesto!!.id ?: 0L, request) {
                    mostrarDefinitivo = false
                }
            }
        )
    }
}

@Composable
private fun ContenidoDetalle(
    cita: Cita,
    presupuesto: com.raposo.wallpint.model.Presupuesto?,
    permitirAccionesAdmin: Boolean,
    onCambiarEstado: (String) -> Unit,
    onAbrirAsignar: () -> Unit,
    onAbrirDefinitivo: () -> Unit
) {
    val estilo = estiloEstadoCita(cita.estado)
    val fechaFormateada = remember(cita.fechaHora) { formatearFecha(cita.fechaHora) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        // Hero
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
                Text(fechaFormateada, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Franja: ${cita.franja}", fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))
            }
        }

        Spacer(Modifier.height(20.dp))

        // Cliente
        Seccion("DATOS DEL CLIENTE")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                FilaInfo(Icons.Default.Person, "Nombre",
                    cita.clienteNombre?.trim()?.ifBlank { "Cliente #${cita.clienteId ?: "?"}" }
                        ?: "Cliente #${cita.clienteId ?: "?"}")
                if (!cita.clienteTelefono.isNullOrBlank()) {
                    Spacer(Modifier.height(10.dp))
                    FilaInfo(Icons.Default.Phone, "Teléfono", cita.clienteTelefono)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Pintores asignados
        Seccion("PINTORES ASIGNADOS (${cita.pintores.size})")
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
                        Text("Sin pintores asignados todavía.", fontSize = 13.sp, color = GrisTexto)
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
                                    Text(p.telefono, fontSize = 12.sp, color = GrisTexto)
                                }
                            }
                        }
                    }
                }
                if (permitirAccionesAdmin) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onAbrirAsignar,
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AzulMedio),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, AzulMedio)
                    ) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(if (cita.pintores.isEmpty()) "Asignar pintores" else "Modificar asignación", fontWeight = FontWeight.Bold)
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(cita.presupuestoReferencia, fontWeight = FontWeight.Bold, color = AzulOscuro)
                            cita.presupuestoTotal?.let {
                                Text("Total estimado: %.2f €".format(it), fontSize = 13.sp, color = GrisTexto)
                            }
                        }
                    }
                    if (presupuesto != null) {
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            MiniStat("Superficie", "${presupuesto.totalM2 ?: 0.0} m²", Modifier.weight(1f))
                            MiniStat("Pintura", "${presupuesto.litrosPintura ?: 0} L", Modifier.weight(1f))
                            MiniStat("Horas", "${presupuesto.horasEstimadas ?: 0.0} h", Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "${presupuesto.estancias.size} estancia${if (presupuesto.estancias.size != 1) "s" else ""}",
                            fontSize = 12.sp,
                            color = GrisTexto,
                            fontWeight = FontWeight.Bold
                        )
                        presupuesto.estancias.forEach { e ->
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "• ${e.nombre} · ${e.ancho}×${e.largo}×${e.alto} m · ${e.estadoParedes.name.lowercase()}",
                                fontSize = 12.sp, color = GrisTexto
                            )
                        }

                        // Acción: tras la visita, marcar como definitivo
                        if (presupuesto.estado == EstadoPresupuesto.ORIENTATIVO) {
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = onAbrirDefinitivo,
                                modifier = Modifier.fillMaxWidth().height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AzulOscuro)
                            ) {
                                Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp), tint = Color.White)
                                Spacer(Modifier.width(8.dp))
                                Text("Convertir en definitivo", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        } else {
                            Spacer(Modifier.height(12.dp))
                            EstadoPresupuestoChip(presupuesto.estado)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }

        if (!cita.notasInternas.isNullOrBlank()) {
            Seccion("NOTAS DEL CLIENTE")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AzulClaro)
            ) {
                Text(cita.notasInternas, modifier = Modifier.padding(16.dp), color = AzulOscuro, fontSize = 13.sp)
            }
            Spacer(Modifier.height(20.dp))
        }

        // Acciones de cambio de estado
        Seccion("CAMBIAR ESTADO")
        AccionesEstado(cita.estado.uppercase(), permitirAccionesAdmin, onCambiarEstado)

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun AccionesEstado(
    estadoActual: String,
    permitirAccionesAdmin: Boolean,
    onCambiar: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            when (estadoActual) {
                "PENDIENTE" -> {
                    if (permitirAccionesAdmin) {
                        AccionBoton(Icons.Default.Check, "Confirmar", Verde) { onCambiar("CONFIRMADA") }
                        AccionBoton(Icons.Default.Close, "Rechazar", Rojo) { onCambiar("CANCELADA") }
                    } else {
                        Text(
                            "Esta cita está pendiente de aprobación por el administrador.",
                            fontSize = 13.sp,
                            color = GrisTexto,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                "CONFIRMADA" -> {
                    AccionBoton(Icons.Default.PlayArrow, "Marcar en curso", AzulMedio) { onCambiar("EN_CURSO") }
                    AccionBoton(Icons.Default.Check, "Marcar completada", Verde) { onCambiar("COMPLETADA") }
                    if (permitirAccionesAdmin) {
                        AccionBoton(Icons.Default.Close, "Cancelar", Rojo) { onCambiar("CANCELADA") }
                    }
                }
                "EN_CURSO" -> {
                    AccionBoton(Icons.Default.Check, "Marcar completada", Verde) { onCambiar("COMPLETADA") }
                    if (permitirAccionesAdmin) {
                        AccionBoton(Icons.Default.Close, "Cancelar", Rojo) { onCambiar("CANCELADA") }
                    }
                }
                else -> {
                    Text(
                        "Esta cita ya está finalizada.",
                        fontSize = 13.sp,
                        color = GrisTexto,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AccionBoton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    texto: String,
    color: Color,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Icon(icon, null, tint = color)
        Spacer(Modifier.width(12.dp))
        Text(texto, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
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
private fun FilaInfo(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, valor: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(AzulClaro),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null, tint = AzulOscuro, modifier = Modifier.size(18.dp)) }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 11.sp, color = GrisTexto, fontWeight = FontWeight.Bold)
            Text(valor, fontWeight = FontWeight.Bold, color = AzulOscuro)
        }
    }
}

@Composable
private fun MiniStat(label: String, valor: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label.uppercase(), fontSize = 9.sp, color = GrisTexto, fontWeight = FontWeight.Bold)
        Text(valor, fontSize = 14.sp, color = AzulOscuro, fontWeight = FontWeight.Bold)
    }
}

private data class EstiloEstado(val texto: String, val fondo: Color, val color: Color)

private fun estiloEstadoCita(estado: String): EstiloEstado = when (estado.uppercase()) {
    "PENDIENTE" -> EstiloEstado("Pendiente", Color(0xFFFEF3C7), Color(0xFF92400E))
    "CONFIRMADA" -> EstiloEstado("Confirmada", Color(0xFFDCFCE7), Color(0xFF166534))
    "EN_CURSO" -> EstiloEstado("En curso", Color(0xFFE0E7FF), Color(0xFF3730A3))
    "COMPLETADA" -> EstiloEstado("Completada", Color(0xFFE5E7EB), Color(0xFF374151))
    "CANCELADA" -> EstiloEstado("Cancelada", Color(0xFFFEE2E2), Color(0xFFB91C1C))
    else -> EstiloEstado(estado, Color(0xFFE5E7EB), GrisTexto)
}

private fun formatearFecha(iso: String): String {
    return try {
        val ldt = LocalDateTime.parse(iso.take(19))
        ldt.format(DateTimeFormatter.ofPattern("EEE d MMM yyyy · HH:mm", Locale("es", "ES")))
            .replaceFirstChar { it.uppercase() }
    } catch (_: Exception) {
        iso
    }
}

@Composable
private fun EstadoPresupuestoChip(estado: EstadoPresupuesto) {
    val (texto, fondo, color) = when (estado) {
        EstadoPresupuesto.ORIENTATIVO -> Triple("ORIENTATIVO", Color(0xFFE0E7FF), Color(0xFF3730A3))
        EstadoPresupuesto.DEFINITIVO -> Triple("DEFINITIVO", AzulClaro, AzulOscuro)
        EstadoPresupuesto.PENDIENTE_ACEPTACION -> Triple("PENDIENTE DE ACEPTACIÓN", Color(0xFFFEF3C7), Color(0xFF92400E))
        EstadoPresupuesto.ACEPTADO -> Triple("ACEPTADO POR EL CLIENTE", Color(0xFFDCFCE7), Color(0xFF166534))
        EstadoPresupuesto.RECHAZADO -> Triple("RECHAZADO POR EL CLIENTE", Color(0xFFFEE2E2), Color(0xFFB91C1C))
    }
    Surface(color = fondo, shape = RoundedCornerShape(8.dp)) {
        Text(
            texto,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            letterSpacing = 0.5.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogMarcarDefinitivo(
    presupuestoActual: com.raposo.wallpint.model.Presupuesto,
    cargando: Boolean,
    onDismiss: () -> Unit,
    onConfirmar: (com.raposo.wallpint.model.MarcarDefinitivoRequest) -> Unit
) {
    // Pre-rellenamos con los valores orientativos para que el pintor solo
    // ajuste lo que cambia en la visita.
    var m2 by remember { mutableStateOf(presupuestoActual.totalM2?.let { "%.2f".format(it) } ?: "") }
    var litros by remember { mutableStateOf(presupuestoActual.litrosPintura?.toString() ?: "") }
    var horas by remember { mutableStateOf(presupuestoActual.horasEstimadas?.let { "%.1f".format(it) } ?: "") }
    var pintores by remember { mutableStateOf(presupuestoActual.numPintores?.toString() ?: "") }
    var materiales by remember { mutableStateOf(presupuestoActual.costeMateriales?.let { "%.2f".format(it) } ?: "") }
    var manoObra by remember { mutableStateOf(presupuestoActual.costeManoObra?.let { "%.2f".format(it) } ?: "") }
    var notas by remember { mutableStateOf("") }

    val matD = materiales.replace(",", ".").toDoubleOrNull()
    val manoD = manoObra.replace(",", ".").toDoubleOrNull()

    // Total calculado en directo a partir de los costes desglosados (con IVA al 21%)
    val totalCalculado: Double? = if (matD != null && manoD != null && matD >= 0 && manoD >= 0) {
        (matD + manoD) * 1.21
    } else null

    val valido = !cargando && totalCalculado != null && totalCalculado > 0

    AlertDialog(
        onDismissRequest = { if (!cargando) onDismiss() },
        title = {
            Column {
                Text("Presupuesto definitivo", fontWeight = FontWeight.Bold, color = AzulOscuro)
                Text(
                    "Ajusta los valores tras la visita técnica",
                    fontSize = 12.sp,
                    color = GrisTexto,
                    fontWeight = FontWeight.Normal
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bloque técnico
                Text("DATOS TÉCNICOS", fontSize = 10.sp, color = AzulMedio, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NumeroField(
                        label = "Superficie (m²)",
                        value = m2,
                        onChange = { m2 = it },
                        decimal = true,
                        modifier = Modifier.weight(1f)
                    )
                    NumeroField(
                        label = "Litros pintura",
                        value = litros,
                        onChange = { litros = it.filter { c -> c.isDigit() } },
                        decimal = false,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NumeroField(
                        label = "Horas estimadas",
                        value = horas,
                        onChange = { horas = it },
                        decimal = true,
                        modifier = Modifier.weight(1f)
                    )
                    NumeroField(
                        label = "Núm. pintores",
                        value = pintores,
                        onChange = { pintores = it.filter { c -> c.isDigit() } },
                        decimal = false,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Bloque económico
                Text("COSTES (sin IVA)", fontSize = 10.sp, color = AzulMedio, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                NumeroField(
                    label = "Materiales (€)",
                    value = materiales,
                    onChange = { materiales = it },
                    decimal = true,
                    modifier = Modifier.fillMaxWidth()
                )
                NumeroField(
                    label = "Mano de obra (€)",
                    value = manoObra,
                    onChange = { manoObra = it },
                    decimal = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Total calculado
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AzulOscuro)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("TOTAL CON IVA (21%)", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                            Text(
                                if (totalCalculado != null) "%.2f €".format(totalCalculado)
                                else "—",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            "Antes: %.2f €".format(presupuestoActual.total ?: 0.0),
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }

                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it },
                    label = { Text("Notas internas (opcional)") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = valido,
                onClick = {
                    onConfirmar(
                        com.raposo.wallpint.model.MarcarDefinitivoRequest(
                            nuevoTotal = totalCalculado,
                            totalM2 = m2.replace(",", ".").toDoubleOrNull(),
                            litrosPintura = litros.toIntOrNull(),
                            horasEstimadas = horas.replace(",", ".").toDoubleOrNull(),
                            numPintores = pintores.toIntOrNull(),
                            costeMateriales = matD,
                            costeManoObra = manoD,
                            notas = notas.takeIf { it.isNotBlank() }
                        )
                    )
                }
            ) {
                if (cargando) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = AzulMedio)
                } else {
                    Text("Confirmar", color = AzulMedio, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(enabled = !cargando, onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun NumeroField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    decimal: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, fontSize = 12.sp) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (decimal) KeyboardType.Decimal else KeyboardType.Number
        ),
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AsignarPintoresSheet(
    seleccionadosIniciales: List<Long>,
    disponibles: List<PintorResumen>,
    onDismiss: () -> Unit,
    onGuardar: (List<Long>) -> Unit
) {
    var seleccionados by remember(seleccionadosIniciales) { mutableStateOf(seleccionadosIniciales.toSet()) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
            Text("Asignar pintores", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AzulOscuro)
            Text(
                "Selecciona uno o varios pintores. Los marcados serán los responsables de la cita.",
                fontSize = 12.sp,
                color = GrisTexto,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            if (disponibles.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) { Text("No hay pintores disponibles", color = GrisTexto) }
            } else {
                Column(modifier = Modifier.heightIn(max = 380.dp).verticalScroll(rememberScrollState())) {
                    disponibles.forEach { p ->
                        val seleccionado = p.id in seleccionados
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (seleccionado) AzulClaro else Color(0xFFF9FAFB)
                            ),
                            border = if (seleccionado) androidx.compose.foundation.BorderStroke(1.5.dp, AzulMedio) else null,
                            onClick = {
                                seleccionados = if (seleccionado) seleccionados - p.id else seleccionados + p.id
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(40.dp).clip(CircleShape).background(if (seleccionado) AzulMedio else Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (seleccionado) {
                                        Icon(Icons.Default.Check, null, tint = Color.White)
                                    } else {
                                        Text(p.nombre.firstOrNull()?.uppercase() ?: "?", fontWeight = FontWeight.Bold, color = AzulOscuro)
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(p.nombreCompleto, fontWeight = FontWeight.Bold, color = AzulOscuro)
                                    if (!p.telefono.isNullOrBlank()) {
                                        Text(p.telefono, fontSize = 12.sp, color = GrisTexto)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { onGuardar(seleccionados.toList()) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulOscuro)
            ) {
                Text("Guardar (${seleccionados.size})", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
