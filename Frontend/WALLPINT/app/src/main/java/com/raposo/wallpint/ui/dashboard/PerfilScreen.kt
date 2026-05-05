package com.raposo.wallpint.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import com.raposo.wallpint.ui.auth.AuthViewModel

private val AzulOscuro = Color(0xFF002B5B)
private val AzulMedio = Color(0xFF0066CC)
private val AzulClaro = Color(0xFFD9F0FA)
private val GrisTexto = Color(0xFF6B7280)
private val Rojo = Color(0xFFB91C1C)

@Composable
fun VistaPerfil(
    rol: String,
    nombreUsuario: String,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    onEditarPerfil: () -> Unit = {},
    onCambiarPassword: () -> Unit = {}
) {
    val perfil by authViewModel.perfil.collectAsState()
    val cargando by authViewModel.cargandoPerfil.collectAsState()

    LaunchedEffect(Unit) { authViewModel.cargarPerfil() }

    var mostrarConfirmarLogout by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // Cabecera con avatar + nombre + rol
        Spacer(Modifier.height(24.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val iniciales = remember(perfil, nombreUsuario) {
                val nombre = perfil?.nombre ?: nombreUsuario
                val apellido = perfil?.apellidos
                val a = nombre.firstOrNull()?.uppercaseChar() ?: '?'
                val b = apellido?.firstOrNull()?.uppercaseChar() ?: ""
                "$a$b"
            }
            Box(
                modifier = Modifier.size(96.dp).clip(CircleShape).background(AzulOscuro),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    iniciales,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = perfil?.let { "${it.nombre} ${it.apellidos}".trim() } ?: nombreUsuario,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro
            )
            EtiquetaRol(rol)
        }

        Spacer(Modifier.height(24.dp))

        // ===== Mis datos =====
        SeccionTitulo("MIS DATOS")
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            if (cargando && perfil == null) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AzulMedio, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
                }
            } else {
                Column(modifier = Modifier.padding(20.dp)) {
                    FilaDato(Icons.Default.Email, "Email", perfil?.email ?: "—")
                    Spacer(Modifier.height(14.dp))
                    FilaDato(Icons.Default.Phone, "Teléfono", perfil?.telefono ?: "—")
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ===== Ajustes =====
        SeccionTitulo("AJUSTES DE LA CUENTA")
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                AjusteItem(
                    icon = Icons.Default.Edit,
                    titulo = "Editar perfil",
                    subtitulo = "Cambia tu nombre, apellidos o teléfono",
                    onClick = onEditarPerfil
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                AjusteItem(
                    icon = Icons.Default.Lock,
                    titulo = "Cambiar contraseña",
                    subtitulo = "Actualiza tu contraseña de acceso",
                    onClick = onCambiarPassword
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ===== Acerca de =====
        SeccionTitulo("ACERCA DE")
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                FilaInfo(Icons.Default.Info, "Versión", "1.0.0")
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                FilaInfo(Icons.Default.Email, "Soporte", "soporte@wallpint.com")
            }
        }

        Spacer(Modifier.height(28.dp))

        // ===== Cerrar sesión =====
        OutlinedButton(
            onClick = { mostrarConfirmarLogout = true },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Rojo),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Rojo)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Cerrar sesión", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(24.dp))
    }

    if (mostrarConfirmarLogout) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmarLogout = false },
            title = { Text("Cerrar sesión", fontWeight = FontWeight.Bold) },
            text = { Text("¿Seguro que quieres cerrar sesión? Tendrás que volver a iniciarla.") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarConfirmarLogout = false
                    onLogout()
                }) {
                    Text("Cerrar sesión", color = Rojo, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmarLogout = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun EtiquetaRol(rol: String) {
    val (texto, color) = when (rol.uppercase()) {
        "CLIENTE" -> "CLIENTE" to AzulMedio
        "PINTOR" -> "PINTOR" to Color(0xFF166534)
        "ADMIN" -> "ADMINISTRADOR" to Color(0xFF92400E)
        else -> rol.uppercase() to GrisTexto
    }
    Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(20.dp)) {
        Text(
            text = texto,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun SeccionTitulo(texto: String) {
    Text(
        text = texto,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = AzulMedio,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    )
}

@Composable
private fun FilaDato(icon: ImageVector, label: String, valor: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(AzulClaro),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null, tint = AzulOscuro, modifier = Modifier.size(18.dp)) }
        Spacer(Modifier.width(14.dp))
        Column {
            Text(label, fontSize = 11.sp, color = GrisTexto, fontWeight = FontWeight.Bold)
            Text(valor, fontSize = 14.sp, color = AzulOscuro, fontWeight = FontWeight.SemiBold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AjusteItem(
    icon: ImageVector,
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(AzulClaro),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = AzulOscuro, modifier = Modifier.size(18.dp)) }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, fontWeight = FontWeight.Bold, color = AzulOscuro, fontSize = 14.sp)
                Text(subtitulo, fontSize = 12.sp, color = GrisTexto)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = GrisTexto)
        }
    }
}

@Composable
private fun FilaInfo(icon: ImageVector, label: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = GrisTexto, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(14.dp))
        Text(label, fontSize = 13.sp, color = GrisTexto, modifier = Modifier.weight(1f))
        Text(valor, fontSize = 13.sp, color = AzulOscuro, fontWeight = FontWeight.SemiBold)
    }
}
