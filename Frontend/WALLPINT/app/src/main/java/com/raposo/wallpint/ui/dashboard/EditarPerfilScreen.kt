package com.raposo.wallpint.ui.dashboard

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raposo.wallpint.ui.auth.AuthViewModel

private val AzulOscuro = Color(0xFF002B5B)
private val AzulMedio = Color(0xFF0066CC)
private val FondoApp = Color(0xFFF4F7FB)
private val GrisCampo = Color(0xFF8A9BB0)
private val GrisTexto = Color(0xFF6B7280)
private val Rojo = Color(0xFFB91C1C)

private fun validarObligatorio(v: String): String? =
    if (v.isBlank()) "Obligatorio" else null

private fun validarTelefonoEdit(v: String): String? = when {
    v.isBlank() -> "Obligatorio"
    !v.all { it.isDigit() } -> "Solo números"
    v.length != 9 -> "Debe tener 9 dígitos"
    else -> null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val perfil by authViewModel.perfil.collectAsState()
    val accion by authViewModel.accionPerfil.collectAsState()
    val error by authViewModel.errorPerfil.collectAsState()
    val mensaje by authViewModel.mensajePerfil.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (perfil == null) authViewModel.cargarPerfil()
    }

    var nombre by remember(perfil) { mutableStateOf(perfil?.nombre ?: "") }
    var apellidos by remember(perfil) { mutableStateOf(perfil?.apellidos ?: "") }
    var telefono by remember(perfil) { mutableStateOf(perfil?.telefono ?: "") }
    var touchedNombre by remember { mutableStateOf(false) }
    var touchedApellidos by remember { mutableStateOf(false) }
    var touchedTelefono by remember { mutableStateOf(false) }

    val errN = validarObligatorio(nombre)
    val errA = validarObligatorio(apellidos)
    val errT = validarTelefonoEdit(telefono)
    val valido = errN == null && errA == null && errT == null

    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
            authViewModel.consumirMensajePerfil()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar perfil", fontWeight = FontWeight.Bold, color = AzulOscuro) },
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                "Email: ${perfil?.email ?: "—"}",
                fontSize = 12.sp,
                color = GrisTexto
            )
            Text(
                "El email no se puede cambiar.",
                fontSize = 11.sp,
                color = GrisTexto
            )

            Spacer(Modifier.height(8.dp))

            CampoEdit(
                value = nombre,
                onChange = { nombre = it; touchedNombre = true },
                label = "Nombre",
                icon = Icons.Default.Person,
                error = errN, touched = touchedNombre
            )
            CampoEdit(
                value = apellidos,
                onChange = { apellidos = it; touchedApellidos = true },
                label = "Apellidos",
                icon = Icons.Default.Person,
                error = errA, touched = touchedApellidos
            )
            CampoEdit(
                value = telefono,
                onChange = { telefono = it.take(9); touchedTelefono = true },
                label = "Teléfono",
                icon = Icons.Default.Phone,
                error = errT, touched = touchedTelefono,
                keyboardType = KeyboardType.Phone
            )

            if (error != null) {
                Surface(
                    color = Rojo.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, null, tint = Rojo, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(error!!, color = Rojo, fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    touchedNombre = true; touchedApellidos = true; touchedTelefono = true
                    if (valido) {
                        authViewModel.editarPerfil(nombre.trim(), apellidos.trim(), telefono) {
                            onBack()
                        }
                    }
                },
                enabled = !accion && valido,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulOscuro)
            ) {
                if (accion) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text("Guardar cambios", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun CampoEdit(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    error: String?,
    touched: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, color = GrisCampo) },
        leadingIcon = { Icon(icon, null, tint = GrisCampo) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        isError = touched && error != null,
        supportingText = {
            if (touched && error != null) {
                Text(error, color = Rojo, fontSize = 12.sp)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

