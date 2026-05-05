package com.raposo.wallpint.ui.presupuesto

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raposo.wallpint.model.EstadoPresupuesto
import com.raposo.wallpint.model.Presupuesto

private val AzulOscuro = Color(0xFF002B5B)
private val AzulMedio = Color(0xFF0066CC)
private val AzulClaro = Color(0xFFD9F0FA)
private val FondoApp = Color(0xFFF4F7FB)
private val GrisTexto = Color(0xFF6B7280)
private val Verde = Color(0xFF10B981)
private val Rojo = Color(0xFFB91C1C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePresupuestoScreen(
    presupuestoId: Long,
    clienteId: Long,
    viewModel: PresupuestoViewModel,
    onBack: () -> Unit,
    onEliminado: () -> Unit,
    onSolicitarCita: (Long, String?) -> Unit = { _, _ -> }
) {
    val detalle by viewModel.detalle.collectAsState()
    val cargando by viewModel.cargandoDetalle.collectAsState()
    val errorDetalle by viewModel.errorDetalle.collectAsState()
    val eliminando by viewModel.eliminando.collectAsState()
    val descargandoPdf by viewModel.descargandoPdf.collectAsState()
    val errorPdf by viewModel.errorPdf.collectAsState()
    val accionEnCurso by viewModel.accionEnCurso.collectAsState()
    var mostrarConfirmar by remember { mutableStateOf(false) }
    var mostrarConfirmarRechazo by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(presupuestoId) {
        viewModel.cargarDetalle(presupuestoId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle", fontWeight = FontWeight.Bold, color = AzulOscuro) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = AzulOscuro)
                    }
                },
                actions = {
                    if (detalle != null) {
                        IconButton(onClick = { mostrarConfirmar = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Rojo)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FondoApp)
            )
        },
        containerColor = FondoApp
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                cargando -> CircularProgressIndicator(color = AzulMedio)
                errorDetalle != null -> ErrorCard(errorDetalle!!)
                detalle != null -> ContenidoDetalle(
                    p = detalle!!,
                    descargandoPdf = descargandoPdf,
                    errorPdf = errorPdf,
                    accionEnCurso = accionEnCurso,
                    onSolicitarCita = { onSolicitarCita(detalle!!.id ?: 0L, detalle!!.referencia) },
                    onDescargarPdf = {
                        val p = detalle ?: return@ContenidoDetalle
                        val id = p.id ?: return@ContenidoDetalle
                        viewModel.descargarYAbrirPdf(id, p.referencia, context)
                    },
                    onAceptar = {
                        val p = detalle ?: return@ContenidoDetalle
                        val id = p.id ?: return@ContenidoDetalle
                        viewModel.responder(id, true) {}
                    },
                    onRechazar = { mostrarConfirmarRechazo = true }
                )
            }
        }
    }

    if (mostrarConfirmar) {
        AlertDialog(
            onDismissRequest = { if (!eliminando) mostrarConfirmar = false },
            title = { Text("Eliminar presupuesto", fontWeight = FontWeight.Bold) },
            text = { Text("¿Seguro que quieres eliminar este presupuesto? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    enabled = !eliminando,
                    onClick = {
                        viewModel.eliminarPresupuesto(presupuestoId, clienteId) {
                            mostrarConfirmar = false
                            onEliminado()
                        }
                    }
                ) {
                    if (eliminando) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Rojo)
                    } else {
                        Text("Eliminar", color = Rojo, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !eliminando,
                    onClick = { mostrarConfirmar = false }
                ) { Text("Cancelar") }
            }
        )
    }

    if (mostrarConfirmarRechazo) {
        AlertDialog(
            onDismissRequest = { if (!accionEnCurso) mostrarConfirmarRechazo = false },
            title = { Text("Rechazar presupuesto", fontWeight = FontWeight.Bold) },
            text = {
                Text("¿Seguro que quieres rechazar este presupuesto? El equipo dejará de planificar el trabajo.")
            },
            confirmButton = {
                TextButton(
                    enabled = !accionEnCurso,
                    onClick = {
                        viewModel.responder(presupuestoId, false) {
                            mostrarConfirmarRechazo = false
                        }
                    }
                ) {
                    if (accionEnCurso) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Rojo)
                    } else {
                        Text("Rechazar", color = Rojo, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(enabled = !accionEnCurso, onClick = { mostrarConfirmarRechazo = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun ErrorCard(mensaje: String) {
    Card(
        modifier = Modifier.padding(24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Rojo)
            Spacer(Modifier.width(12.dp))
            Text(mensaje, color = Rojo, fontSize = 13.sp)
        }
    }
}

@Composable
private fun ContenidoDetalle(
    p: Presupuesto,
    descargandoPdf: Boolean,
    errorPdf: String?,
    accionEnCurso: Boolean,
    onSolicitarCita: () -> Unit,
    onDescargarPdf: () -> Unit,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        // Hero
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = AzulOscuro)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Verde),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(24.dp)) }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        p.estado.name.replace("_", " "),
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(20.dp))
                Text(p.referencia, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                p.fechaSolicitud?.take(10)?.let {
                    Text(it, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "%.2f €".format(p.total ?: 0.0),
                    color = Color.White,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("IVA incluido", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(20.dp))

        // Stats
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(Icons.Default.Star, "Superficie", "${p.totalM2 ?: 0.0} m²", Modifier.weight(1f))
            StatCard(Icons.Default.Build, "Pintura", "${p.litrosPintura ?: 0} L", Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(Icons.Default.DateRange, "Horas", "${p.horasEstimadas ?: 0.0} h", Modifier.weight(1f))
            StatCard(Icons.Default.Person, "Pintores", "${p.numPintores ?: 0}", Modifier.weight(1f))
        }

        Spacer(Modifier.height(24.dp))

        Text("DESGLOSE", fontSize = 11.sp, letterSpacing = 1.sp, color = AzulMedio, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Linea("Materiales", p.costeMateriales)
                Spacer(Modifier.height(8.dp))
                Linea("Mano de obra", p.costeManoObra)
                Spacer(Modifier.height(8.dp))
                Linea("IVA (21%)", p.iva)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                Linea("Total", p.total, destacado = true)
            }
        }

        Spacer(Modifier.height(20.dp))

        Text("ESTANCIAS (${p.estancias.size})", fontSize = 11.sp, letterSpacing = 1.sp, color = AzulMedio, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        p.estancias.forEach { est ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(AzulClaro),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.Home, null, tint = AzulOscuro) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(est.nombre, fontWeight = FontWeight.Bold, color = AzulOscuro)
                        Text(
                            "${est.ancho}×${est.largo}×${est.alto} m · ${est.estadoParedes.name.lowercase()}",
                            fontSize = 12.sp, color = GrisTexto
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Banner del estado del presupuesto (excepto ORIENTATIVO que es la vista normal)
        if (p.estado != EstadoPresupuesto.ORIENTATIVO) {
            BannerEstadoCliente(estado = p.estado, total = p.total ?: 0.0)
            Spacer(Modifier.height(16.dp))
        }

        // Acciones según el estado
        when (p.estado) {
            EstadoPresupuesto.ORIENTATIVO -> {
                Button(
                    onClick = onSolicitarCita,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulMedio)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Solicitar cita", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            EstadoPresupuesto.PENDIENTE_ACEPTACION -> {
                // Acciones críticas: aceptar (verde) o rechazar (rojo)
                Button(
                    onClick = onAceptar,
                    enabled = !accionEnCurso,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    if (accionEnCurso) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                    } else {
                        Icon(Icons.Default.Check, null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Aceptar presupuesto", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onRechazar,
                    enabled = !accionEnCurso,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Rojo),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Rojo)
                ) {
                    Icon(Icons.Default.Close, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Rechazar", fontWeight = FontWeight.Bold)
                }
            }
            else -> { /* ACEPTADO/RECHAZADO/DEFINITIVO sin acciones */ }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onDescargarPdf,
            enabled = !descargandoPdf,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AzulMedio),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, AzulMedio)
        ) {
            if (descargandoPdf) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = AzulMedio)
                Spacer(Modifier.width(10.dp))
                Text("Generando PDF…", fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Descargar PDF", fontWeight = FontWeight.Bold)
            }
        }

        if (errorPdf != null) {
            Spacer(Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, null, tint = Color(0xFFB91C1C))
                    Spacer(Modifier.width(8.dp))
                    Text(errorPdf, color = Color(0xFFB91C1C), fontSize = 13.sp)
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun StatCard(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(AzulClaro),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = AzulOscuro, modifier = Modifier.size(18.dp)) }
            Spacer(Modifier.height(12.dp))
            Text(label, fontSize = 11.sp, color = GrisTexto, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 18.sp, color = AzulOscuro, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BannerEstadoCliente(estado: EstadoPresupuesto, total: Double) {
    val (titulo, mensaje, color) = when (estado) {
        EstadoPresupuesto.PENDIENTE_ACEPTACION -> Triple(
            "Precio definitivo tras la visita",
            "El equipo ha ajustado el presupuesto. Acéptalo para programar el trabajo o recházalo si no te encaja.",
            Color(0xFFF59E0B)
        )
        EstadoPresupuesto.ACEPTADO -> Triple(
            "Presupuesto aceptado",
            "Hemos recibido tu confirmación. Te contactaremos para coordinar el inicio.",
            Color(0xFF10B981)
        )
        EstadoPresupuesto.RECHAZADO -> Triple(
            "Presupuesto rechazado",
            "Has rechazado este presupuesto. Si cambias de opinión, contáctanos.",
            Rojo
        )
        EstadoPresupuesto.DEFINITIVO -> Triple(
            "Presupuesto definitivo",
            "Este es el precio final acordado.",
            AzulMedio
        )
        else -> Triple("", "", AzulMedio)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                Spacer(Modifier.width(8.dp))
                Text(titulo, fontWeight = FontWeight.Bold, color = color, fontSize = 14.sp)
            }
            Spacer(Modifier.height(6.dp))
            Text(mensaje, fontSize = 12.sp, color = GrisTexto, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun Linea(label: String, valor: Double?, destacado: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = if (destacado) 16.sp else 14.sp,
            color = if (destacado) AzulOscuro else GrisTexto,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            "%.2f €".format(valor ?: 0.0),
            fontSize = if (destacado) 18.sp else 14.sp,
            color = AzulOscuro,
            fontWeight = FontWeight.Bold
        )
    }
}
