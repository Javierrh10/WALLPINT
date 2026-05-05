package com.raposo.wallpint.ui.presupuesto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raposo.wallpint.model.EstanciaRequest

private val AzulOscuro = Color(0xFF002B5B)
private val AzulMedio = Color(0xFF0066CC)
private val FondoApp = Color(0xFFF4F7FB)
private val GrisTexto = Color(0xFF6B7280)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoPresupuestoStep3Screen(
    viewModel: PresupuestoViewModel,
    clienteId: Long,
    onBack: () -> Unit,
    onCalculado: () -> Unit
) {
    val datos by viewModel.datosGenerales.collectAsState()
    val estancias by viewModel.estanciasNuevas.collectAsState()
    val calculando by viewModel.calculando.collectAsState()
    val errorCalc by viewModel.errorCalculo.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmar", fontWeight = FontWeight.Bold, color = AzulOscuro) },
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
                .padding(horizontal = 24.dp)
        ) {
            ProgresoPasos(pasoActual = 3)

            Spacer(Modifier.height(24.dp))

            Text("Resumen del proyecto", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
            Spacer(Modifier.height(8.dp))
            Text(
                "Revisa los datos antes de calcular el presupuesto orientativo.",
                fontSize = 14.sp,
                color = GrisTexto
            )

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                CardResumen(titulo = "DATOS GENERALES") {
                    FilaIcono(Icons.Default.Home, "Proyecto", datos.nombreProyecto.ifBlank { "—" })
                    Spacer(Modifier.height(8.dp))
                    FilaIcono(Icons.Default.LocationOn, "Dirección", datos.direccion.ifBlank { "—" })
                }

                Spacer(Modifier.height(16.dp))

                CardResumen(titulo = "ESTANCIAS (${estancias.size})") {
                    estancias.forEachIndexed { i, e ->
                        if (i > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        FilaEstancia(e)
                    }
                }

                if (errorCalc != null) {
                    Spacer(Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            errorCalc!!,
                            color = Color(0xFFB91C1C),
                            modifier = Modifier.padding(12.dp),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }

            Button(
                onClick = { viewModel.calcular(clienteId, onCalculado) },
                enabled = !calculando && estancias.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulMedio)
            ) {
                if (calculando) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(12.dp))
                    Text("Calculando…", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Calcular presupuesto", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CardResumen(titulo: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(titulo, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AzulMedio, letterSpacing = 1.sp)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun FilaIcono(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(36.dp).background(Color(0xFFD9F0FA), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) { Icon(icon, contentDescription = null, tint = AzulOscuro, modifier = Modifier.size(18.dp)) }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 11.sp, color = GrisTexto)
            Text(value, fontSize = 14.sp, color = AzulOscuro, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun FilaEstancia(e: EstanciaRequest) {
    Column {
        Text(e.nombre, fontWeight = FontWeight.Bold, color = AzulOscuro)
        Text(
            "${e.ancho}×${e.largo}×${e.alto} m · paredes ${e.estadoParedes.name.lowercase()}",
            fontSize = 12.sp, color = GrisTexto
        )
        Text(
            "Puertas: ${e.numPuertas} · Ventanas: ${e.numVentanas} · Capas: ${e.numCapas}${if (e.incluirTecho) " · Techo" else ""}",
            fontSize = 12.sp, color = GrisTexto
        )
    }
}
