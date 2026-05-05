package com.raposo.wallpint.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
// IMPORTANTE: Importamos tu modelo y tu ViewModel
import com.raposo.wallpint.model.Presupuesto
import com.raposo.wallpint.model.EstadoPresupuesto
import com.raposo.wallpint.ui.presupuesto.PresupuestoViewModel

private val AzulOscuro = Color(0xFF002B5B)
private val AzulMedio = Color(0xFF0066CC)
private val AzulClaro = Color(0xFFD9F0FA)
private val FondoApp = Color(0xFFF4F7FB)
private val GrisTexto = Color(0xFF6B7280)
private val GrisFondoTarjeta = Color(0xFFF3F4F6)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun VistaHomeCliente(
    nombreUsuario: String,
    clienteId: Long,
    viewModel: PresupuestoViewModel,
    onNuevoPresupuesto: () -> Unit = {},
    onAbrirDetalle: (Long) -> Unit = {}
) {
    // 1. "Escuchamos" los datos que nos manda el ViewModel
    val presupuestos by viewModel.presupuestos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // 2. Carga inicial al abrir la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarPresupuestosCliente(clienteId)
    }

    // 3. Pull-to-refresh: el usuario puede arrastrar hacia abajo para recargar
    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = { viewModel.cargarPresupuestosCliente(clienteId) },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 80.dp)) {
        // ... (Tu código de la cabecera y el gradiente sigue igual) ...
        item {
            Box(modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(colors = listOf(Color.White, FondoApp), startY = 0f, endY = 300f)).padding(horizontal = 24.dp, vertical = 24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(AzulOscuro), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = "Avatar", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Hola, $nombreUsuario", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
                        }
                    }
                    Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = AzulOscuro)
                }
            }
        }

        // ... (Tu código de Acciones Rápidas sigue igual) ...
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(text = "ACCIONES RÁPIDAS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AzulMedio, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = AzulOscuro),
                    onClick = onNuevoPresupuesto
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                        Column {
                            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Edit, contentDescription = "Calculadora", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Calcular nuevo\npresupuesto", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 28.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Obtén una estimación instantánea para tu próximo proyecto de pintura.", fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))
                            Spacer(modifier = Modifier.weight(1f))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Empezar ahora", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = "Ir", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Placeholder honesto para citas (se sustituirá en la Fase 5)
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text("PRÓXIMA CITA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AzulMedio, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(44.dp).clip(CircleShape).background(AzulClaro),
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Default.DateRange, contentDescription = null, tint = AzulOscuro) }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Sin citas programadas", fontWeight = FontWeight.Bold, color = AzulOscuro, fontSize = 14.sp)
                            Text(
                                "Solicita una cita desde un presupuesto.",
                                fontSize = 12.sp, color = GrisTexto
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // 3. LA MAGIA OCURRE AQUÍ: Mostramos los datos reales
        item {
            Column {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("PRESUPUESTOS RECIENTES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AzulMedio, letterSpacing = 1.sp)
                    Text("Ver todos", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Mostramos estado según lo que diga el servidor
                when {
                    isLoading && presupuestos.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = AzulMedio)
                        }
                    }
                    error != null -> {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFB91C1C))
                                Spacer(Modifier.width(12.dp))
                                Text(error!!, color = Color(0xFFB91C1C), fontSize = 13.sp)
                            }
                        }
                    }
                    presupuestos.isEmpty() -> {
                        EmptyStatePresupuestos(onCrearNuevo = onNuevoPresupuesto)
                    }
                    else -> {
                        LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(presupuestos) { presupuesto ->
                                CardPresupuestoReal(
                                    presupuesto = presupuesto,
                                    onClick = { presupuesto.id?.let { onAbrirDetalle(it) } }
                                )
                            }
                        }
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun EmptyStatePresupuestos(onCrearNuevo: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(AzulClaro),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Edit, contentDescription = null, tint = AzulOscuro) }
            Spacer(Modifier.height(12.dp))
            Text("Aún no tienes presupuestos", fontWeight = FontWeight.Bold, color = AzulOscuro)
            Spacer(Modifier.height(4.dp))
            Text(
                "Calcula tu primer presupuesto en menos de un minuto.",
                fontSize = 13.sp,
                color = GrisTexto,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onCrearNuevo,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulMedio)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Crear presupuesto", fontWeight = FontWeight.Bold)
            }
        }
    }
}

private data class EstadoEstilo(val texto: String, val fondo: Color, val texto_color: Color)

private fun estiloEstado(estado: EstadoPresupuesto): EstadoEstilo = when (estado) {
    EstadoPresupuesto.ORIENTATIVO -> EstadoEstilo("Orientativo", Color(0xFFE0E7FF), Color(0xFF3730A3))
    EstadoPresupuesto.DEFINITIVO -> EstadoEstilo("Definitivo", Color(0xFFD9F0FA), Color(0xFF075985))
    EstadoPresupuesto.PENDIENTE_ACEPTACION -> EstadoEstilo("Pendiente", Color(0xFFFEF3C7), Color(0xFF92400E))
    EstadoPresupuesto.ACEPTADO -> EstadoEstilo("Aceptado", Color(0xFFDCFCE7), Color(0xFF166534))
    EstadoPresupuesto.RECHAZADO -> EstadoEstilo("Rechazado", Color(0xFFFEE2E2), Color(0xFFB91C1C))
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CardPresupuestoReal(presupuesto: Presupuesto, onClick: () -> Unit = {}) {
    val precioFormateado = presupuesto.total?.let { "%.2f €".format(it) } ?: "Pendiente"
    val m2Texto = presupuesto.totalM2?.let { "%.1f m²".format(it) } ?: "—"
    val fechaCorta = presupuesto.fechaSolicitud?.take(10) ?: ""
    val estilo = estiloEstado(presupuesto.estado)

    Card(
        modifier = Modifier.width(240.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(AzulClaro), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Home, contentDescription = null, tint = AzulOscuro, modifier = Modifier.size(20.dp))
                }
                Surface(color = estilo.fondo, shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = estilo.texto.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = estilo.texto_color,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        letterSpacing = 0.5.sp
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(presupuesto.referencia, fontSize = 12.sp, color = GrisTexto, maxLines = 1)
            Spacer(Modifier.height(2.dp))
            Text(precioFormateado, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = GrisTexto, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(m2Texto, fontSize = 12.sp, color = GrisTexto)
                if (fechaCorta.isNotEmpty()) {
                    Spacer(Modifier.width(12.dp))
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = GrisTexto, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(fechaCorta, fontSize = 12.sp, color = GrisTexto)
                }
            }
        }
    }
}