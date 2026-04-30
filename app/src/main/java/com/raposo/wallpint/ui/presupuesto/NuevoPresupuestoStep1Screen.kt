package com.raposo.wallpint.ui.presupuesto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AzulOscuro = Color(0xFF002B5B)
private val AzulMedio = Color(0xFF0066CC)
private val FondoApp = Color(0xFFF4F7FB)
private val GrisTexto = Color(0xFF6B7280)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoPresupuestoStep1Screen(
    viewModel: PresupuestoViewModel,
    onBack: () -> Unit,
    onContinuar: () -> Unit
) {
    val datos by viewModel.datosGenerales.collectAsState()
    var nombreProyecto by remember { mutableStateOf(datos.nombreProyecto) }
    var direccion by remember { mutableStateOf(datos.direccion) }

    val puedeContinuar = nombreProyecto.isNotBlank() && direccion.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo presupuesto", fontWeight = FontWeight.Bold, color = AzulOscuro) },
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
            ProgresoPasos(pasoActual = 1)

            Spacer(Modifier.height(24.dp))

            Text("Datos generales", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
            Spacer(Modifier.height(8.dp))
            Text(
                "Cuéntanos los detalles básicos del proyecto.",
                fontSize = 14.sp,
                color = GrisTexto
            )

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = nombreProyecto,
                onValueChange = { nombreProyecto = it },
                label = { Text("Nombre del proyecto") },
                placeholder = { Text("Ej. Reforma piso Velázquez") },
                leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección") },
                placeholder = { Text("Calle, número, ciudad") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.setDatosGenerales(nombreProyecto.trim(), direccion.trim())
                    onContinuar()
                },
                enabled = puedeContinuar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulMedio)
            ) {
                Text("Continuar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProgresoPasos(pasoActual: Int, total: Int = 3) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i in 1..total) {
            val activo = i <= pasoActual
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(if (activo) AzulMedio else Color(0xFFE5E7EB))
            )
        }
    }
    Spacer(Modifier.height(8.dp))
    Text(
        text = "Paso $pasoActual de $total",
        fontSize = 12.sp,
        color = GrisTexto,
        fontWeight = FontWeight.Bold
    )
}
