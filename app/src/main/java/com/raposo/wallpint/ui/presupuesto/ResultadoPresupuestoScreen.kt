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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AzulOscuro = Color(0xFF002B5B)
private val AzulMedio = Color(0xFF0066CC)
private val AzulClaro = Color(0xFFD9F0FA)
private val FondoApp = Color(0xFFF4F7FB)
private val GrisTexto = Color(0xFF6B7280)
private val Verde = Color(0xFF10B981)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultadoPresupuestoScreen(
    viewModel: PresupuestoViewModel,
    onCerrar: () -> Unit
) {
    val resultado by viewModel.resultado.collectAsState()
    val datos by viewModel.datosGenerales.collectAsState()

    if (resultado == null) {
        Box(modifier = Modifier.fillMaxSize().background(FondoApp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AzulMedio)
        }
        return
    }

    val r = resultado!!

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Presupuesto", fontWeight = FontWeight.Bold, color = AzulOscuro) },
                navigationIcon = {
                    IconButton(onClick = onCerrar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cerrar", tint = AzulOscuro)
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
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
                        Text("PRESUPUESTO ORIENTATIVO", fontSize = 11.sp, letterSpacing = 1.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(20.dp))
                    Text(datos.nombreProyecto.ifBlank { r.referencia }, color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
                    Text(r.referencia, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "%.2f €".format(r.total ?: 0.0),
                        color = Color.White,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("IVA incluido", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            // Stats grid
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(Icons.Default.Star, "Superficie", "${r.totalM2 ?: 0.0} m²", Modifier.weight(1f))
                StatCard(Icons.Default.Build, "Pintura", "${r.litrosPintura ?: 0} L", Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(Icons.Default.DateRange, "Horas", "${r.horasEstimadas ?: 0.0} h", Modifier.weight(1f))
                StatCard(Icons.Default.Person, "Pintores", "${r.numPintores ?: 0}", Modifier.weight(1f))
            }

            Spacer(Modifier.height(24.dp))

            // Desglose
            Text("DESGLOSE", fontSize = 11.sp, letterSpacing = 1.sp, color = AzulMedio, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    LineaPrecio("Materiales", r.costeMateriales)
                    Spacer(Modifier.height(8.dp))
                    LineaPrecio("Mano de obra", r.costeManoObra)
                    Spacer(Modifier.height(8.dp))
                    LineaPrecio("IVA (21%)", r.iva)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    LineaPrecio("Total", r.total, destacado = true)
                }
            }

            Spacer(Modifier.height(20.dp))

            // Estancias incluidas
            Text("ESTANCIAS INCLUIDAS", fontSize = 11.sp, letterSpacing = 1.sp, color = AzulMedio, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            r.estancias.forEach { est ->
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

            Button(
                onClick = onCerrar,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulMedio)
            ) {
                Text("Volver al inicio", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(32.dp))
        }
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
private fun LineaPrecio(label: String, valor: Double?, destacado: Boolean = false) {
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
