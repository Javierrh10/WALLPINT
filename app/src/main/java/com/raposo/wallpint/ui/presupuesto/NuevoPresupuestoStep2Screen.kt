package com.raposo.wallpint.ui.presupuesto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.KeyboardType
import com.raposo.wallpint.model.EstadoPared
import com.raposo.wallpint.model.EstanciaRequest

private val AzulOscuro = Color(0xFF002B5B)
private val AzulMedio = Color(0xFF0066CC)
private val AzulClaro = Color(0xFFD9F0FA)
private val FondoApp = Color(0xFFF4F7FB)
private val GrisTexto = Color(0xFF6B7280)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoPresupuestoStep2Screen(
    viewModel: PresupuestoViewModel,
    onBack: () -> Unit,
    onContinuar: () -> Unit
) {
    val estancias by viewModel.estanciasNuevas.collectAsState()
    var mostrarDialog by remember { mutableStateOf(false) }

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
        containerColor = FondoApp,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { mostrarDialog = true },
                containerColor = AzulMedio,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Añadir estancia", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            ProgresoPasos(pasoActual = 2)

            Spacer(Modifier.height(24.dp))

            Text("Estancias", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
            Spacer(Modifier.height(8.dp))
            Text(
                "Añade cada habitación a pintar. Cuantas más añadas, más preciso será el cálculo.",
                fontSize = 14.sp,
                color = GrisTexto
            )

            Spacer(Modifier.height(16.dp))

            if (estancias.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text("No has añadido ninguna estancia", color = GrisTexto)
                        Text("Pulsa el botón para empezar", color = GrisTexto, fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
                ) {
                    itemsIndexed(estancias) { index, est ->
                        EstanciaCard(
                            estancia = est,
                            onEliminar = { viewModel.removeEstancia(index) }
                        )
                    }
                }
            }

            Button(
                onClick = onContinuar,
                enabled = estancias.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulMedio)
            ) {
                Text("Continuar (${estancias.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (mostrarDialog) {
        DialogAddEstancia(
            onDismiss = { mostrarDialog = false },
            onConfirm = { nueva ->
                viewModel.addEstancia(nueva)
                mostrarDialog = false
            }
        )
    }
}

@Composable
private fun EstanciaCard(estancia: EstanciaRequest, onEliminar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(AzulClaro),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Home, contentDescription = null, tint = AzulOscuro)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(estancia.nombre, fontWeight = FontWeight.Bold, color = AzulOscuro)
                Text(
                    "${estancia.ancho}×${estancia.largo}×${estancia.alto} m · ${estancia.estadoParedes.name}",
                    fontSize = 12.sp,
                    color = GrisTexto
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red.copy(alpha = 0.7f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogAddEstancia(
    onDismiss: () -> Unit,
    onConfirm: (EstanciaRequest) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var ancho by remember { mutableStateOf("") }
    var largo by remember { mutableStateOf("") }
    var alto by remember { mutableStateOf("2.5") }
    var estado by remember { mutableStateOf(EstadoPared.NUEVO) }
    var puertas by remember { mutableStateOf("1") }
    var ventanas by remember { mutableStateOf("1") }
    var capas by remember { mutableStateOf("1") }
    var techo by remember { mutableStateOf(false) }
    var color by remember { mutableStateOf("") }

    val valido = nombre.isNotBlank() &&
        ancho.toDoubleOrNull()?.let { it > 0 } == true &&
        largo.toDoubleOrNull()?.let { it > 0 } == true &&
        alto.toDoubleOrNull()?.let { it > 0 } == true

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = valido,
                onClick = {
                    onConfirm(
                        EstanciaRequest(
                            nombre = nombre.trim(),
                            ancho = ancho.toDouble(),
                            largo = largo.toDouble(),
                            alto = alto.toDouble(),
                            estadoParedes = estado,
                            numPuertas = puertas.toIntOrNull() ?: 0,
                            numVentanas = ventanas.toIntOrNull() ?: 0,
                            color = color.takeIf { it.isNotBlank() },
                            numCapas = (capas.toIntOrNull() ?: 1).coerceIn(1, 2),
                            incluirTecho = techo
                        )
                    )
                }
            ) {
                Text("Añadir", color = AzulMedio, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        title = { Text("Nueva estancia", fontWeight = FontWeight.Bold, color = AzulOscuro) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre (Salón, Cocina...)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NumField("Ancho (m)", ancho, Modifier.weight(1f)) { ancho = it }
                    NumField("Largo (m)", largo, Modifier.weight(1f)) { largo = it }
                    NumField("Alto (m)", alto, Modifier.weight(1f)) { alto = it }
                }
                Spacer(Modifier.height(12.dp))
                Text("Estado de las paredes", fontSize = 12.sp, color = GrisTexto, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EstadoPared.entries.forEach { e ->
                        FilterChip(
                            selected = estado == e,
                            onClick = { estado = e },
                            label = { Text(e.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NumField("Puertas", puertas, Modifier.weight(1f)) { puertas = it }
                    NumField("Ventanas", ventanas, Modifier.weight(1f)) { ventanas = it }
                    NumField("Capas (1-2)", capas, Modifier.weight(1f)) { capas = it }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("Color (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = techo, onCheckedChange = { techo = it })
                    Text("Incluir techo")
                }
            }
        }
    )
}

@Composable
private fun NumField(label: String, value: String, modifier: Modifier = Modifier, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
    )
}

