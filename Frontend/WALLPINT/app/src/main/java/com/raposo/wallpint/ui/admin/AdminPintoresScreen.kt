package com.raposo.wallpint.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.raposo.wallpint.model.PintorResumen

private val AzulOscuro = Color(0xFF002B5B)
private val AzulMedio = Color(0xFF0066CC)
private val AzulClaro = Color(0xFFD9F0FA)
private val FondoApp = Color(0xFFF4F7FB)
private val GrisTexto = Color(0xFF6B7280)
private val Verde = Color(0xFF10B981)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPintoresScreen(
    viewModel: GestionUsuariosViewModel,
    onAbrirPintor: (Long) -> Unit = {}
) {
    val pintores by viewModel.pintores.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()

    var consulta by remember { mutableStateOf("") }
    var soloActivos by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.cargarPintores() }

    val filtrados = remember(pintores, consulta, soloActivos) {
        pintores.filter { p ->
            val matchTexto = consulta.isBlank() ||
                    p.nombreCompleto.contains(consulta, true) ||
                    (p.email?.contains(consulta, true) == true) ||
                    (p.telefono?.contains(consulta) == true)
            val matchActivo = !soloActivos || (p.activo == true)
            matchTexto && matchActivo
        }
    }

    val activos = pintores.count { it.activo == true }
    val inactivos = pintores.count { it.activo == false }

    Column(modifier = Modifier.fillMaxSize().background(FondoApp)) {

        // Resumen rápido
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ContadorChip("Activos", activos.toString(), Verde, Modifier.weight(1f))
            ContadorChip("Inactivos", inactivos.toString(), GrisTexto, Modifier.weight(1f))
        }

        // Buscador
        OutlinedTextField(
            value = consulta,
            onValueChange = { consulta = it },
            placeholder = { Text("Buscar por nombre, email…") },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = GrisTexto) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 4.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = soloActivos,
                onClick = { soloActivos = !soloActivos },
                label = { Text("Solo activos") },
                leadingIcon = if (soloActivos) {
                    { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
                } else null
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "${filtrados.size} resultado${if (filtrados.size != 1) "s" else ""}",
                fontSize = 11.sp,
                color = GrisTexto,
                fontWeight = FontWeight.Bold
            )
        }

        PullToRefreshBox(
            isRefreshing = cargando,
            onRefresh = { viewModel.cargarPintores() },
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                error != null -> Card(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(error!!, color = Color(0xFFB91C1C), modifier = Modifier.padding(16.dp), fontSize = 13.sp)
                }
                filtrados.isEmpty() && !cargando -> Box(
                    modifier = Modifier.fillMaxSize().padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Build, null, tint = Color.LightGray, modifier = Modifier.size(56.dp))
                        Spacer(Modifier.height(12.dp))
                        Text(
                            if (pintores.isEmpty()) "No hay pintores registrados"
                            else "Ningún pintor coincide con los filtros",
                            color = GrisTexto
                        )
                    }
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtrados, key = { it.id }) { p ->
                        PintorCard(
                            pintor = p,
                            onToggleActivo = { nuevo -> viewModel.cambiarActivo(p.id, nuevo) },
                            onClick = { onAbrirPintor(p.id) }
                        )
                    }
                    item { Spacer(Modifier.height(40.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ContadorChip(label: String, valor: String, color: Color, modifier: Modifier = Modifier) {
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
                Text(label.uppercase(), fontSize = 10.sp, color = GrisTexto, fontWeight = FontWeight.Bold)
            }
            Text(valor, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PintorCard(
    pintor: PintorResumen,
    onToggleActivo: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    val estaActivo = pintor.activo == true
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape)
                    .background(if (estaActivo) AzulClaro else Color(0xFFE5E7EB)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    pintor.nombre.firstOrNull()?.uppercase() ?: "?",
                    fontWeight = FontWeight.Bold,
                    color = if (estaActivo) AzulOscuro else GrisTexto
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    pintor.nombreCompleto,
                    fontWeight = FontWeight.Bold,
                    color = if (estaActivo) AzulOscuro else GrisTexto
                )
                if (!pintor.email.isNullOrBlank()) {
                    Text(pintor.email, fontSize = 12.sp, color = GrisTexto)
                }
                if (!pintor.telefono.isNullOrBlank()) {
                    Text("Tel: ${pintor.telefono}", fontSize = 12.sp, color = GrisTexto)
                }
                Spacer(Modifier.height(4.dp))
                Surface(
                    color = if (estaActivo) Verde.copy(alpha = 0.1f) else Color(0xFFE5E7EB),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        if (estaActivo) "ACTIVO" else "INACTIVO",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (estaActivo) Verde else GrisTexto,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        letterSpacing = 0.5.sp
                    )
                }
            }
            Switch(
                checked = estaActivo,
                onCheckedChange = onToggleActivo,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Verde
                )
            )
        }
    }
}
