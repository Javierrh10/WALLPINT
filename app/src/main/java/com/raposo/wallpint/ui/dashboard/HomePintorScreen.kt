package com.raposo.wallpint.ui.dashboard

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AzulOscuro = Color(0xFF002B5B)
private val AzulMedio = Color(0xFF0066CC)
private val FondoApp = Color(0xFFF4F7FB)
private val GrisTexto = Color(0xFF6B7280)
private val GrisFondoTarjeta = Color(0xFFF3F4F6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VistaPintor(nombreUsuario: String) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 80.dp)) {
        item {
            Box(modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(colors = listOf(Color.White, FondoApp), startY = 0f, endY = 300f)).padding(horizontal = 24.dp, vertical = 24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(AzulOscuro), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = "Avatar", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Hola, $nombreUsuario", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
                        }
                    }
                    BadgedBox(badge = { Badge(containerColor = Color.Red) { Text("3", color = Color.White) } }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = AzulOscuro)
                    }
                }
            }
        }
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(text = "RESUMEN DE ACTIVIDAD", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AzulMedio, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 12.dp))
                Card(modifier = Modifier.fillMaxWidth().height(220.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = AzulOscuro)) {
                    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                        Column {
                            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Email, contentDescription = "Solicitudes", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Tienes 3 nuevas\nsolicitudes", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 28.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Hay clientes esperando tu valoración cerca de tu zona de trabajo.", fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))
                            Spacer(modifier = Modifier.weight(1f))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Revisar solicitudes", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = "Ir", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("PRÓXIMO TRABAJO", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AzulMedio, letterSpacing = 1.sp)
                    Text("Ver agenda", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = GrisFondoTarjeta), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF2E7D32)))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("HOY - EN RUTA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                            Text("10:30", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Quitar gotelé y alisar", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
                        Text("Cliente: María López", fontSize = 14.sp, color = GrisTexto)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.LocationOn, contentDescription = "Lugar", tint = AzulOscuro, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Avenida de la Ilustración, 12\n28029 Madrid", fontSize = 13.sp, color = GrisTexto, lineHeight = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(16.dp)).background(Color.LightGray), contentAlignment = Alignment.Center) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Place, contentDescription = "Mapa", tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Abrir en Google Maps", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}