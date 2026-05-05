package com.raposo.wallpint.ui.cita

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raposo.wallpint.model.FranjaCita
import com.raposo.wallpint.ui.presupuesto.PresupuestoViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val AzulOscuro = Color(0xFF002B5B)
private val AzulMedio = Color(0xFF0066CC)
private val AzulClaro = Color(0xFFD9F0FA)
private val AzulSelDia = Color(0xFFE8F0FF)
private val FondoApp = Color(0xFFF4F7FB)
private val GrisTexto = Color(0xFF6B7280)
private val GrisDeshabilitado = Color(0xFFCBD5E1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitarCitaScreen(
    clienteId: Long,
    presupuestoId: Long,                 // SIEMPRE obligatorio
    presupuestoReferencia: String?,
    viewModel: CitaViewModel,
    presupuestoViewModel: PresupuestoViewModel,
    onBack: () -> Unit,
    onCreada: () -> Unit
) {
    val creando by viewModel.creando.collectAsState()
    val error by viewModel.errorCreacion.collectAsState()
    val detalle by presupuestoViewModel.detalle.collectAsState()
    val citasExistentes by viewModel.citas.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(presupuestoId) {
        presupuestoViewModel.cargarDetalle(presupuestoId)
    }
    LaunchedEffect(clienteId) {
        // Cargamos las citas del cliente para saber qué huecos ya tiene reservados
        // y poder desactivar las franjas correspondientes.
        viewModel.cargarCitasCliente(clienteId)
    }

    var mesVisible by remember { mutableStateOf(YearMonth.now()) }
    var fechaSeleccionada by remember { mutableStateOf<LocalDate?>(null) }
    var franjaSeleccionada by remember { mutableStateOf<FranjaCita?>(null) }

    val puedeConfirmar = fechaSeleccionada != null && franjaSeleccionada != null && !creando

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("GESTIÓN DE CITAS", fontSize = 10.sp, color = AzulMedio, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text("Calendario de Reservas", fontWeight = FontWeight.Bold, color = AzulOscuro, fontSize = 20.sp)
                    }
                },
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    "Selecciona una fecha disponible para programar tu servicio de pintura.",
                    fontSize = 13.sp,
                    color = GrisTexto,
                    lineHeight = 18.sp
                )

                Spacer(Modifier.height(20.dp))

                Calendario(
                    mes = mesVisible,
                    seleccionada = fechaSeleccionada,
                    onMesAnterior = { mesVisible = mesVisible.minusMonths(1) },
                    onMesSiguiente = { mesVisible = mesVisible.plusMonths(1) },
                    onDiaSeleccionado = { fechaSeleccionada = it }
                )

                Spacer(Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(28.dp).clip(CircleShape).background(AzulOscuro),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.DateRange, null, tint = Color.White, modifier = Modifier.size(16.dp)) }
                    Spacer(Modifier.width(10.dp))
                    Text("Horarios disponibles", fontWeight = FontWeight.Bold, color = AzulOscuro)
                }
                Spacer(Modifier.height(12.dp))

                // Calculamos las franjas que el cliente YA tiene reservadas para
                // la fecha seleccionada (ignorando las CANCELADAS).
                val franjasOcupadas = remember(citasExistentes, fechaSeleccionada) {
                    val fecha = fechaSeleccionada ?: return@remember emptySet()
                    citasExistentes.asSequence()
                        .filter { !it.estado.equals("CANCELADA", ignoreCase = true) }
                        .filter {
                            try {
                                LocalDateTime.parse(it.fechaHora.take(19)).toLocalDate() == fecha
                            } catch (_: Exception) { false }
                        }
                        .map { it.franja.lowercase() }
                        .toSet()
                }

                val mananaOcupada = "mañana" in franjasOcupadas || "manana" in franjasOcupadas
                val tardeOcupada = "tarde" in franjasOcupadas

                // Si la franja seleccionada se ocupa por una recarga, la deseleccionamos
                LaunchedEffect(mananaOcupada, tardeOcupada, franjaSeleccionada) {
                    if (franjaSeleccionada == FranjaCita.MANANA && mananaOcupada) franjaSeleccionada = null
                    if (franjaSeleccionada == FranjaCita.TARDE && tardeOcupada) franjaSeleccionada = null
                }

                FranjaSlot(
                    franja = FranjaCita.MANANA,
                    horario = "09:00 – 14:00",
                    seleccionada = franjaSeleccionada == FranjaCita.MANANA,
                    ocupada = mananaOcupada,
                    onClick = { franjaSeleccionada = FranjaCita.MANANA }
                )
                Spacer(Modifier.height(10.dp))
                FranjaSlot(
                    franja = FranjaCita.TARDE,
                    horario = "16:00 – 20:00",
                    seleccionada = franjaSeleccionada == FranjaCita.TARDE,
                    ocupada = tardeOcupada,
                    onClick = { franjaSeleccionada = FranjaCita.TARDE }
                )

                Spacer(Modifier.height(20.dp))

                val detalleActivo = detalle?.takeIf { it.id == presupuestoId }
                PresupuestoInfoCard(
                    referencia = presupuestoReferencia ?: detalleActivo?.referencia ?: "—",
                    numEstancias = detalleActivo?.estancias?.size,
                    color = detalleActivo?.estancias?.firstOrNull()?.color
                )

                if (error != null) {
                    Spacer(Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = Color(0xFFB91C1C))
                            Spacer(Modifier.width(8.dp))
                            Text(error!!, color = Color(0xFFB91C1C), fontSize = 13.sp)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
            }

            // Botón fijo abajo
            Surface(
                color = FondoApp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                    Button(
                        onClick = {
                            val fecha = fechaSeleccionada ?: return@Button
                            val franja = franjaSeleccionada ?: return@Button
                            val hora = if (franja == FranjaCita.MANANA) LocalTime.of(9, 0) else LocalTime.of(16, 0)
                            val fechaHora = LocalDateTime.of(fecha, hora)
                            val iso = fechaHora.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            viewModel.crearCita(
                                clienteId = clienteId,
                                presupuestoId = presupuestoId,
                                fechaHoraIso = iso,
                                franja = franja.etiqueta,
                                notas = null,
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Cita solicitada para el ${fecha.dayOfMonth}/${fecha.monthValue} (${franja.etiqueta})",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    onCreada()
                                }
                            )
                        },
                        enabled = puedeConfirmar,
                        modifier = Modifier.fillMaxWidth().height(58.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulOscuro)
                    ) {
                        if (creando) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                            Spacer(Modifier.width(12.dp))
                            Text("Reservando…", fontWeight = FontWeight.Bold, color = Color.White)
                        } else {
                            Text("Confirmar reserva", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ===== Calendario =====

@Composable
private fun Calendario(
    mes: YearMonth,
    seleccionada: LocalDate?,
    onMesAnterior: () -> Unit,
    onMesSiguiente: () -> Unit,
    onDiaSeleccionado: (LocalDate) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Cabecera mes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMesAnterior) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Mes anterior", tint = AzulOscuro)
                }
                Text(
                    mes.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES")))
                        .replaceFirstChar { it.uppercase() },
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro,
                    fontSize = 16.sp
                )
                IconButton(onClick = onMesSiguiente) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Mes siguiente", tint = AzulOscuro)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Días de la semana
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM").forEach { dia ->
                    Text(
                        dia,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        color = GrisTexto,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Grid de días
            val primerDia = mes.atDay(1)
            // En Java time: MONDAY=1, SUNDAY=7. Queremos 0..6 con lunes=0
            val offsetInicial = primerDia.dayOfWeek.value - 1
            val diasMes = mes.lengthOfMonth()
            val totalCeldas = ((offsetInicial + diasMes + 6) / 7) * 7
            val hoy = LocalDate.now()

            for (semana in 0 until totalCeldas / 7) {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                    for (col in 0..6) {
                        val celdaIdx = semana * 7 + col
                        val numDia = celdaIdx - offsetInicial + 1
                        if (numDia in 1..diasMes) {
                            val fecha = mes.atDay(numDia)
                            val esPasado = fecha.isBefore(hoy)
                            val esFinde = fecha.dayOfWeek == DayOfWeek.SATURDAY || fecha.dayOfWeek == DayOfWeek.SUNDAY
                            val disponible = !esPasado && !esFinde
                            val esSeleccionado = fecha == seleccionada

                            Box(
                                modifier = Modifier.weight(1f).aspectRatio(1f).padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CeldaDia(
                                    numero = numDia,
                                    disponible = disponible,
                                    seleccionado = esSeleccionado,
                                    onClick = if (disponible) {
                                        { onDiaSeleccionado(fecha) }
                                    } else null
                                )
                            }
                        } else {
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CeldaDia(
    numero: Int,
    disponible: Boolean,
    seleccionado: Boolean,
    onClick: (() -> Unit)?
) {
    val (fondo, colorTexto) = when {
        seleccionado -> AzulOscuro to Color.White
        disponible -> AzulSelDia to AzulOscuro
        else -> Color.Transparent to GrisDeshabilitado
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(if (seleccionado) 50 else 12))
            .background(fondo)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            numero.toString(),
            color = colorTexto,
            fontSize = 14.sp,
            fontWeight = if (seleccionado || disponible) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ===== Slot de franja =====

@Composable
private fun FranjaSlot(
    franja: FranjaCita,
    horario: String,
    seleccionada: Boolean,
    ocupada: Boolean,
    onClick: () -> Unit
) {
    val containerColor = when {
        ocupada -> Color(0xFFF3F4F6)
        seleccionada -> AzulSelDia
        else -> Color.White
    }
    val textColor = if (ocupada) GrisTexto else AzulOscuro
    val border = if (seleccionada && !ocupada)
        androidx.compose.foundation.BorderStroke(1.dp, AzulMedio) else null

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = border,
        enabled = !ocupada,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Star, null,
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${franja.etiqueta} $horario",
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                if (ocupada) {
                    Text(
                        "Ya tienes una cita en esta franja",
                        fontSize = 11.sp,
                        color = GrisTexto
                    )
                }
            }
            when {
                ocupada -> Icon(Icons.Default.Lock, null, tint = GrisTexto, modifier = Modifier.size(20.dp))
                seleccionada -> Box(
                    modifier = Modifier.size(28.dp).clip(CircleShape).background(AzulOscuro),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(18.dp)) }
            }
        }
    }
}

// ===== Card de presupuesto =====

@Composable
private fun PresupuestoInfoCard(referencia: String, numEstancias: Int?, color: String?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AzulSelDia),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(AzulMedio),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Servicio asociado", fontWeight = FontWeight.Bold, color = AzulOscuro, fontSize = 14.sp)
                Text(
                    buildString {
                        append("Presupuesto $referencia")
                        if (numEstancias != null) append(" · $numEstancias estancia${if (numEstancias != 1) "s" else ""}")
                    },
                    fontSize = 12.sp,
                    color = GrisTexto
                )
                if (!color.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val parsed = parseHexColorOrNull(color)
                        if (parsed != null) {
                            Box(
                                modifier = Modifier.size(12.dp).clip(CircleShape).background(parsed)
                            )
                            Spacer(Modifier.width(6.dp))
                        }
                        Text(color, fontSize = 12.sp, color = AzulOscuro, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun parseHexColorOrNull(hex: String): Color? {
    val limpio = hex.removePrefix("#")
    if (limpio.length != 6) return null
    return try {
        Color(android.graphics.Color.parseColor("#$limpio"))
    } catch (_: IllegalArgumentException) {
        null
    }
}
