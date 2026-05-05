package com.raposo.wallpint.ui.dashboard

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raposo.wallpint.ui.auth.AuthViewModel

private val AzulOscuro = Color(0xFF002B5B)
private val FondoApp = Color(0xFFF4F7FB)
private val GrisCampo = Color(0xFF8A9BB0)
private val GrisTexto = Color(0xFF6B7280)
private val Rojo = Color(0xFFB91C1C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CambiarPasswordScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val accion by authViewModel.accionPerfil.collectAsState()
    val error by authViewModel.errorPerfil.collectAsState()
    val mensaje by authViewModel.mensajePerfil.collectAsState()
    val context = LocalContext.current

    var actual by remember { mutableStateOf("") }
    var nueva by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var verActual by remember { mutableStateOf(false) }
    var verNueva by remember { mutableStateOf(false) }

    var touchedActual by remember { mutableStateOf(false) }
    var touchedNueva by remember { mutableStateOf(false) }
    var touchedConfirmar by remember { mutableStateOf(false) }

    val errActual = if (actual.isBlank()) "Obligatorio" else null
    val errNueva = when {
        nueva.isBlank() -> "Obligatorio"
        nueva.length < 6 -> "Mínimo 6 caracteres"
        nueva == actual -> "Debe ser distinta de la actual"
        else -> null
    }
    val errConfirmar = when {
        confirmar.isBlank() -> "Obligatorio"
        confirmar != nueva -> "No coincide con la nueva contraseña"
        else -> null
    }
    val valido = errActual == null && errNueva == null && errConfirmar == null

    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
            authViewModel.consumirMensajePerfil()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cambiar contraseña", fontWeight = FontWeight.Bold, color = AzulOscuro) },
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
                "Por seguridad debes introducir tu contraseña actual antes de elegir una nueva.",
                fontSize = 13.sp,
                color = GrisTexto,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(8.dp))

            CampoPassword(
                value = actual,
                onChange = { actual = it; touchedActual = true },
                label = "Contraseña actual",
                visible = verActual,
                onToggleVisible = { verActual = !verActual },
                error = errActual, touched = touchedActual
            )
            CampoPassword(
                value = nueva,
                onChange = { nueva = it; touchedNueva = true },
                label = "Nueva contraseña",
                visible = verNueva,
                onToggleVisible = { verNueva = !verNueva },
                error = errNueva, touched = touchedNueva
            )
            CampoPassword(
                value = confirmar,
                onChange = { confirmar = it; touchedConfirmar = true },
                label = "Repite la nueva contraseña",
                visible = verNueva,
                onToggleVisible = { verNueva = !verNueva },
                error = errConfirmar, touched = touchedConfirmar
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
                    touchedActual = true; touchedNueva = true; touchedConfirmar = true
                    if (valido) {
                        authViewModel.cambiarPassword(actual, nueva) {
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
                    Text("Guardar contraseña", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun CampoPassword(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onToggleVisible: () -> Unit,
    error: String?,
    touched: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, color = GrisCampo) },
        leadingIcon = { Icon(Icons.Default.Lock, null, tint = GrisCampo) },
        trailingIcon = {
            IconButton(onClick = onToggleVisible) {
                Icon(Icons.Default.Lock, "Mostrar", tint = GrisCampo)
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        isError = touched && error != null,
        supportingText = {
            if (touched && error != null) {
                Text(error, color = Rojo, fontSize = 12.sp)
            }
        }
    )
}
