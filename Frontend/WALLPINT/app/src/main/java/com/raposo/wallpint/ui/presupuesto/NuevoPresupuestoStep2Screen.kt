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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
    var editandoIndex by remember { mutableStateOf<Int?>(null) }

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
                    contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
                ) {
                    itemsIndexed(estancias) { index, est ->
                        EstanciaCard(
                            estancia = est,
                            onEditar = {
                                editandoIndex = index
                                mostrarDialog = true
                            },
                            onEliminar = { viewModel.removeEstancia(index) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Botón secundario: añadir estancia (queda encima del botón de continuar)
            OutlinedButton(
                onClick = {
                    editandoIndex = null
                    mostrarDialog = true
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AzulMedio),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AzulMedio)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Añadir estancia", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(8.dp))

            // Botón principal: continuar
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
        val inicial = editandoIndex?.let { estancias.getOrNull(it) }
        DialogAddEstancia(
            inicial = inicial,
            onDismiss = {
                mostrarDialog = false
                editandoIndex = null
            },
            onConfirm = { nueva ->
                val idx = editandoIndex
                if (idx != null) viewModel.editarEstancia(idx, nueva)
                else viewModel.addEstancia(nueva)
                mostrarDialog = false
                editandoIndex = null
            }
        )
    }
}

@Composable
private fun EstanciaCard(
    estancia: EstanciaRequest,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
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
                    "${estancia.ancho}×${estancia.largo}×${estancia.alto} m · ${estancia.estadoParedes.name.lowercase()}",
                    fontSize = 12.sp,
                    color = GrisTexto
                )
            }
            IconButton(onClick = onEditar) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = AzulMedio)
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red.copy(alpha = 0.7f))
            }
        }
    }
}

private data class ColorPreset(val nombre: String, val hex: String)

private val ColoresPredefinidos = listOf(
    ColorPreset("Blanco", "#FFFFFF"),
    ColorPreset("Crema", "#F5F0E1"),
    ColorPreset("Beige", "#E8DDC4"),
    ColorPreset("Gris claro", "#D1D5DB"),
    ColorPreset("Gris", "#9CA3AF"),
    ColorPreset("Azul claro", "#BFDBFE"),
    ColorPreset("Azul", "#60A5FA"),
    ColorPreset("Verde menta", "#BBF7D0"),
    ColorPreset("Rosa palo", "#FBCFE8"),
    ColorPreset("Amarillo", "#FEF08A"),
    ColorPreset("Terracota", "#D97757"),
    ColorPreset("Negro", "#111827")
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun DialogAddEstancia(
    inicial: EstanciaRequest? = null,
    onDismiss: () -> Unit,
    onConfirm: (EstanciaRequest) -> Unit
) {
    val esEdicion = inicial != null
    var nombre by remember { mutableStateOf(inicial?.nombre ?: "") }
    var ancho by remember { mutableStateOf(inicial?.ancho?.toString() ?: "") }
    var largo by remember { mutableStateOf(inicial?.largo?.toString() ?: "") }
    var alto by remember { mutableStateOf(inicial?.alto?.toString() ?: "2.5") }
    var estado by remember { mutableStateOf(inicial?.estadoParedes ?: EstadoPared.NUEVO) }
    var puertas by remember { mutableStateOf(inicial?.numPuertas?.toString() ?: "1") }
    var ventanas by remember { mutableStateOf(inicial?.numVentanas?.toString() ?: "1") }
    var capas by remember { mutableStateOf(inicial?.numCapas?.toString() ?: "1") }
    var techo by remember { mutableStateOf(inicial?.incluirTecho ?: false) }
    var colorHex by remember { mutableStateOf(inicial?.color ?: "") }

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
                            color = colorHex.takeIf { it.isNotBlank() },
                            numCapas = (capas.toIntOrNull() ?: 1).coerceIn(1, 2),
                            incluirTecho = techo
                        )
                    )
                }
            ) {
                Text(
                    if (esEdicion) "Guardar" else "Añadir",
                    color = AzulMedio,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        title = {
            Text(
                if (esEdicion) "Editar estancia" else "Nueva estancia",
                fontWeight = FontWeight.Bold,
                color = AzulOscuro
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre (Salón, Cocina…)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                SeccionLabel("Dimensiones (m)")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NumField("Ancho", ancho, Modifier.weight(1f)) { ancho = it }
                    NumField("Largo", largo, Modifier.weight(1f)) { largo = it }
                    NumField("Alto", alto, Modifier.weight(1f)) { alto = it }
                }

                SeccionLabel("Estado de las paredes")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    EstadoPared.entries.forEach { e ->
                        FilterChip(
                            selected = estado == e,
                            onClick = { estado = e },
                            label = { Text(e.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }

                SeccionLabel("Aperturas y capas")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NumField("Puertas", puertas, Modifier.weight(1f)) { puertas = it }
                    NumField("Ventanas", ventanas, Modifier.weight(1f)) { ventanas = it }
                    NumField("Capas", capas, Modifier.weight(1f)) { capas = it }
                }

                SeccionLabel("Color de pintura (opcional)")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ColoresPredefinidos.forEach { preset ->
                        SwatchColor(
                            preset = preset,
                            seleccionado = colorHex.equals(preset.hex, ignoreCase = true),
                            onClick = { colorHex = preset.hex }
                        )
                    }
                }
                OutlinedTextField(
                    value = colorHex,
                    onValueChange = { input ->
                        // Solo acepta caracteres válidos para un hex (#RRGGBB)
                        val limpio = input.uppercase().filter { it == '#' || it in '0'..'9' || it in 'A'..'F' }
                        colorHex = limpio.take(7)
                    },
                    label = { Text("Hex personalizado (#RRGGBB)") },
                    placeholder = { Text("#FFFFFF") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        val previewColor = parseHexColorOrNull(colorHex)
                        if (previewColor != null) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(previewColor)
                                    .border(1.dp, Color.LightGray, CircleShape)
                            )
                        }
                    }
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(checked = techo, onCheckedChange = { techo = it })
                    Text("Incluir techo en el cálculo")
                }
            }
        }
    )
}

@Composable
private fun SeccionLabel(texto: String) {
    Text(
        texto,
        fontSize = 11.sp,
        color = GrisTexto,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
    )
}

@Composable
private fun SwatchColor(
    preset: ColorPreset,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val color = parseHexColorOrNull(preset.hex) ?: Color.LightGray
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (seleccionado) 3.dp else 1.dp,
                color = if (seleccionado) AzulMedio else Color.LightGray,
                shape = CircleShape
            )
            .clickable { onClick() }
    )
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

