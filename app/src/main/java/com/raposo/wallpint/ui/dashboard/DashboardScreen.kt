package com.raposo.wallpint.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AzulOscuro = Color(0xFF003366)
private val AzulMedio = Color(0xFF0066CC)
private val FondoApp = Color(0xFFF8FAFC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    rol: String,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (rol == "CLIENTE") "Encuentra tu Pintor" else "Mi Panel de Trabajo",
                        fontWeight = FontWeight.Bold,
                        color = AzulOscuro
                    )
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FondoApp)
            )
        },
        containerColor = FondoApp
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (rol == "CLIENTE") {
                VistaCliente()
            } else {
                VistaPintor()
            }
        }
    }
}

// --- VISTA DEL CLIENTE ---
@Composable
fun VistaCliente() {
    // Datos falsos para probar la UI
    val pintoresDePrueba = listOf(
        mapOf("nombre" to "Carlos Ruiz", "especialidad" to "Exteriores y Fachadas", "nota" to "4.8"),
        mapOf("nombre" to "Ana Gómez", "especialidad" to "Interiores y Papel Pintado", "nota" to "4.9"),
        mapOf("nombre" to "Luis Martínez", "especialidad" to "Alisado de paredes", "nota" to "4.5")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Pintores destacados",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = AzulOscuro,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )
        }

        items(pintoresDePrueba) { pintor ->
            CardPintor(
                nombre = pintor["nombre"]!!,
                especialidad = pintor["especialidad"]!!,
                nota = pintor["nota"]!!
            )
        }
    }
}

@Composable
fun CardPintor(nombre: String, especialidad: String, nota: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar falso
            Box(
                modifier = Modifier.size(60.dp).clip(CircleShape).background(AzulOscuro.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = AzulMedio, modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(text = nombre, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
                Text(text = especialidad, fontSize = 14.sp, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.Star, contentDescription = "Nota", tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = nota, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Botón
            Button(
                onClick = { /* TODO: Ir al perfil */ },
                colors = ButtonDefaults.buttonColors(containerColor = AzulOscuro),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Ver")
            }
        }
    }
}

// --- VISTA DEL PINTOR ---
@Composable
fun VistaPintor() {
    val solicitudesDePrueba = listOf(
        mapOf("cliente" to "María López", "trabajo" to "Pintar salón 20m2", "estado" to "Pendiente"),
        mapOf("cliente" to "Juan Pérez", "trabajo" to "Quitar gotelé", "estado" to "Aceptado")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Tarjeta de resumen
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = AzulOscuro),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Mi Estado Actual", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    Text("Disponible para trabajar", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                text = "Solicitudes recientes",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = AzulOscuro,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(solicitudesDePrueba) { solicitud ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Cliente: ${solicitud["cliente"]}", fontWeight = FontWeight.Bold, color = AzulOscuro)
                    Text(text = solicitud["trabajo"]!!, color = Color.Gray, modifier = Modifier.padding(vertical = 4.dp))
                    Text(
                        text = "Estado: ${solicitud["estado"]}",
                        color = if (solicitud["estado"] == "Pendiente") Color(0xFFE65100) else Color(0xFF2E7D32),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}