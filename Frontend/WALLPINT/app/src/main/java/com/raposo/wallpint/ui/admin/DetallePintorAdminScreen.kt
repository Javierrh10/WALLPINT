package com.raposo.wallpint.ui.admin

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raposo.wallpint.model.Cita
import com.raposo.wallpint.model.EditarPintorRequest
import com.raposo.wallpint.model.PintorResumen
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePintorAdminScreen(
    pintorId: Long,
    gestionViewModel: GestionUsuariosViewModel,
    citaViewModel: CitaViewModel,
    onBack: () -> Unit,
    onAbrirCita: (Long) -> Unit
) {
    val pintor by gestionViewModel.pintorDetalle.collectAsState()
    val cargando by gestionViewModel.accionPintor.collectAsState()
    val error by gestionViewModel.errorPintor.collectAsState()
    val citas by citaViewModel.citas.collectAsState()

    var mostrarEditar by remember { mutableStateOf(false) }
    var mostrarConfirmacionDesactivar by remember { mutableStateOf(false) }

    LaunchedEffect(pintorId) {
        gestionViewModel.cargarPintorPorId(pintorId)
        citaViewModel.cargarCitasPintor(pintorId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pintor", fontWeight = FontWeight.Bold, color = AzulOscuro) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AzulOscuro)
                    }
                },
                actions = {
                    if (pintor != null) {
                        IconButton(onClick = { mostrarEditar = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = AzulOscuro)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FondoApp)
            )
        },
        containerColor = FondoApp
    ) { padding ->
        if (pintor == null) {
            Box(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (cargando) {
                    CircularProgressIndicator(color = AzulMedio)
                } else if (error != null) {
                    Card(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(error!!, color = Rojo, modifier = Modifier.padding(16.dp), fontSize = 13.sp)
                    }
                }
            }
            return@Scaffold
        }

        val p = pintor!!
        val activos = citas.count { it.estado.uppercase() in listOf("CONFIRMADA", "EN_CURSO") }
        val historial = citas.count { it.estado.uppercase() in listOf("COMPLETADA", "CANCELADA") }

        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Hero
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = AzulOscuro)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(60.dp).clip(CircleShape)
                                .background(if (p.activo == true) AzulClaro else Color(0xFFE5E7EB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                p.nombre.firstOrNull()?.uppercase() ?: "?",
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = AzulOscuro
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(p.nombreCompleto, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                            Text("Pintor · ID ${p.id}", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                            Spacer(Modifier.height(6.dp))
                            val activo = p.activo == true
                            Surface(
                                color = if (activo) Verde.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    if (activo) "ACTIVO" else "INACTIVO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activo) Color(0xFFA7F3D0) else Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }

            // Datos de contacto
            item {
                Seccion("DATOS DE CONTACTO")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        FilaInfo(Icons.Default.Email, "Email", p.email ?: "—")
                        Spacer(Modifier.height(10.dp))
                        FilaInfo(Icons.Default.Phone, "Teléfono", p.telefono ?: "—")
                    }
                }
            }

            // Disponibilidad
            item {
                Seccion("DISPONIBILIDAD")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape)
                                .background(if (p.activo == true) Verde.copy(alpha = 0.15f) else Color(0xFFE5E7EB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (p.activo == true) Icons.Default.Check else Icons.Default.Close,
                                null,
                                tint = if (p.activo == true) Verde else GrisTexto,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                if (p.activo == true) "Pintor activo" else "Pintor inactivo",
                                fontWeight = FontWeight.Bold,
                                color = AzulOscuro
                            )
                            Text(
                                if (p.activo == true)
                                    "Aparece como opción al asignar pintores a citas."
                                else
                                    "No aparece en la lista de asignación.",
                                fontSize = 12.sp,
                                color = GrisTexto
                            )
                        }
                        Switch(
                            checked = p.activo == true,
                            onCheckedChange = { nuevo ->
                                if (!nuevo && activos > 0) {
                                    // Aviso si tiene trabajos activos
                                    mostrarConfirmacionDesactivar = true
                                } else {
                                    gestionViewModel.cambiarActivoConDetalle(p.id, nuevo)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Verde
                            )
                        )
                    }
                }
            }

            // Resumen de trabajos
            item {
                Seccion("RESUMEN DE TRABAJOS")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatBox("Activos", activos.toString(), Verde, Modifier.weight(1f))
                    StatBox("Historial", historial.toString(), GrisTexto, Modifier.weight(1f))
                    StatBox("Total", citas.size.toString(), AzulMedio, Modifier.weight(1f))
                }
            }

            // Trabajos asignados
            item {
                Spacer(Modifier.height(8.dp))
                Seccion("TRABAJOS ASIGNADOS (${citas.size})")
                if (citas.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Build, null, tint = GrisTexto)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Este pintor no tiene trabajos asignados todavía.",
                                fontSize = 13.sp, color = GrisTexto
                            )
                        }
                    }
                }
            }
            items(citas, key = { it.id ?: it.fechaHora }) { cita ->
                TrabajoMini(cita, onClick = { cita.id?.let(onAbrirCita) })
            }

            item { Spacer(Modifier.height(40.dp)) }
        }
    }

    if (mostrarEditar && pintor != null) {
        DialogEditarPintor(
            inicial = pintor!!,
            cargando = cargando,
            error = error,
            onDismiss = {
                gestionViewModel.consumirErrorPintor()
                mostrarEditar = false
            },
            onConfirmar = { req ->
                gestionViewModel.editarPintor(pintorId, req) {
                    mostrarEditar = false
                }
            }
        )
    }

    if (mostrarConfirmacionDesactivar && pintor != null) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacionDesactivar = false },
            title = { Text("Desactivar pintor", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Este pintor tiene trabajos activos asignados. Si lo desactivas " +
                            "no aparecerá al asignar nuevas citas, pero los trabajos " +
                            "actuales se mantendrán. ¿Continuar?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    mostrarConfirmacionDesactivar = false
                    pintor?.id?.let { gestionViewModel.cambiarActivoConDetalle(it, false) }
                }) {
                    Text("Desactivar", color = Rojo, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacionDesactivar = false }) { Text("Cancelar") }
            }
        )
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
        modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
    )
}

@Composable
private fun FilaInfo(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    valor: String
) {
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
private fun StatBox(label: String, valor: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                Spacer(Modifier.width(6.dp))
                Text(label.uppercase(), fontSize = 9.sp, color = GrisTexto, fontWeight = FontWeight.Bold)
            }
            Text(valor, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrabajoMini(cita: Cita, onClick: () -> Unit) {
    val fecha = remember(cita.fechaHora) {
        try {
            LocalDateTime.parse(cita.fechaHora.take(19))
                .format(DateTimeFormatter.ofPattern("EEE d MMM · HH:mm", Locale.forLanguageTag("es-ES")))
                .replaceFirstChar { it.uppercase() }
        } catch (_: Exception) { cita.fechaHora }
    }

    val (badgeFondo, badgeColor) = when (cita.estado.uppercase()) {
        "PENDIENTE" -> Color(0xFFFEF3C7) to Color(0xFF92400E)
        "CONFIRMADA" -> Color(0xFFDCFCE7) to Color(0xFF166534)
        "EN_CURSO" -> Color(0xFFE0E7FF) to Color(0xFF3730A3)
        "COMPLETADA" -> Color(0xFFE5E7EB) to Color(0xFF374151)
        else -> Color(0xFFFEE2E2) to Color(0xFFB91C1C)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(AzulClaro),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.DateRange, null, tint = AzulOscuro, modifier = Modifier.size(18.dp)) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    cita.clienteNombre?.trim()?.ifBlank { "Cliente" } ?: "Cliente",
                    fontWeight = FontWeight.Bold, color = AzulOscuro, fontSize = 13.sp
                )
                Text("$fecha · ${cita.franja}", fontSize = 12.sp, color = GrisTexto)
                if (cita.presupuestoReferencia != null) {
                    Text(
                        "Presupuesto ${cita.presupuestoReferencia}",
                        fontSize = 11.sp, color = GrisTexto
                    )
                }
            }
            Surface(color = badgeFondo, shape = RoundedCornerShape(8.dp)) {
                Text(
                    cita.estado.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun DialogEditarPintor(
    inicial: PintorResumen,
    cargando: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onConfirmar: (EditarPintorRequest) -> Unit
) {
    var nombre by remember { mutableStateOf(inicial.nombre) }
    var apellidos by remember { mutableStateOf(inicial.apellidos.orEmpty()) }
    var email by remember { mutableStateOf(inicial.email.orEmpty()) }
    var telefono by remember { mutableStateOf(inicial.telefono.orEmpty()) }

    val errorEmail = if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        "Email no válido" else null
    val errorTel = when {
        telefono.isBlank() -> "Obligatorio"
        !telefono.all { it.isDigit() } -> "Solo números"
        telefono.length != 9 -> "Debe tener 9 dígitos"
        else -> null
    }
    val valido = nombre.isNotBlank() && apellidos.isNotBlank() &&
            errorEmail == null && errorTel == null && !cargando

    AlertDialog(
        onDismissRequest = { if (!cargando) onDismiss() },
        title = { Text("Editar pintor", fontWeight = FontWeight.Bold, color = AzulOscuro) },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 440.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = nombre, onValueChange = { nombre = it },
                    label = { Text("Nombre") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = apellidos, onValueChange = { apellidos = it },
                    label = { Text("Apellidos") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = email, onValueChange = { email = it.trim() },
                    label = { Text("Email") }, singleLine = true,
                    isError = errorEmail != null && email.isNotBlank(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = telefono, onValueChange = { telefono = it.take(9) },
                    label = { Text("Teléfono (9 dígitos)") }, singleLine = true,
                    isError = errorTel != null && telefono.isNotBlank(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                )
                if (error != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(error, color = Rojo, modifier = Modifier.padding(10.dp), fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = valido,
                onClick = {
                    onConfirmar(
                        EditarPintorRequest(
                            nombre = nombre.trim(),
                            apellidos = apellidos.trim(),
                            email = email.trim(),
                            telefono = telefono
                        )
                    )
                }
            ) {
                if (cargando) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = AzulMedio)
                } else {
                    Text("Guardar", color = AzulMedio, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(enabled = !cargando, onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
