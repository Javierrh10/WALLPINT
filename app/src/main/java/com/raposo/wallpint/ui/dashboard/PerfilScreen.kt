package com.raposo.wallpint.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AzulOscuro = Color(0xFF002B5B)
private val AzulMedio = Color(0xFF0066CC)
private val AzulClaro = Color(0xFFD9F0FA)
private val GrisTexto = Color(0xFF6B7280)
private val GrisFondoTarjeta = Color(0xFFF3F4F6)
private val RojoFondo = Color(0xFFFFEBEE)
private val RojoTexto = Color(0xFFD32F2F)

@Composable
fun VistaPerfil(rol: String, nombreUsuario: String, onLogout: () -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 32.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = AzulOscuro)
                Text("Perfil", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
                Icon(Icons.Default.Settings, contentDescription = "Ajustes", tint = AzulOscuro)
            }
        }
        item {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(AzulOscuro), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = "Avatar", tint = Color.White, modifier = Modifier.size(60.dp))
                    }
                    Box(modifier = Modifier.size(28.dp).offset(x = (-4).dp, y = (-4).dp).clip(CircleShape).background(AzulMedio), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Check, contentDescription = "Verificado", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(nombreUsuario, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
                Text(text = if (rol == "CLIENTE") "CLIENTE PREMIUM" else "PINTOR VERIFICADO", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GrisTexto, letterSpacing = 1.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth().padding(24.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("PERFIL COMPLETO", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AzulOscuro, letterSpacing = 1.sp)
                        Text("85%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AzulMedio)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(progress = { 0.85f }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = AzulMedio, trackColor = GrisFondoTarjeta)
                }
            }
        }
        item {
            ProfileSection(title = "CUENTA") {
                ProfileMenuItem(icon = Icons.Default.Person, title = "Mi Información")
                ProfileMenuItem(icon = Icons.Default.ShoppingCart, title = "Métodos de Pago")
                ProfileMenuItem(icon = Icons.Default.LocationOn, title = "Direcciones", isLast = true)
            }
        }
        item {
            ProfileSection(title = "PREFERENCIAS") {
                ProfileMenuItem(icon = Icons.Default.Notifications, title = "Notificaciones")
                ProfileMenuItem(icon = Icons.Default.Info, title = "Modo Oscuro", trailing = { Switch(checked = false, onCheckedChange = {}, colors = SwitchDefaults.colors(checkedThumbColor = AzulMedio)) })
                ProfileMenuItem(icon = Icons.Default.Search, title = "Idioma", isLast = true, trailing = { Row(verticalAlignment = Alignment.CenterVertically) { Text("ES", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AzulOscuro); Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = GrisTexto) } })
            }
        }
        item {
            ProfileSection(title = "SOPORTE") {
                ProfileMenuItem(icon = Icons.Default.Info, title = "Centro de Ayuda")
                ProfileMenuItem(icon = Icons.Default.List, title = "Términos y Condiciones", isLast = true)
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RojoFondo),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Salir", tint = RojoTexto)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", color = RojoTexto, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Text(text = "WALLPINT V1.0.0", modifier = Modifier.fillMaxWidth().padding(top = 24.dp), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GrisTexto, letterSpacing = 2.sp)
        }
    }
}

@Composable
fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)) {
        Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GrisTexto, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 12.dp, start = 8.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) { content() }
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, isLast: Boolean = false, trailing: @Composable () -> Unit = { Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = GrisTexto) }) {
    Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(AzulClaro), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = AzulMedio, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = AzulOscuro, modifier = Modifier.weight(1f))
        trailing()
    }
}