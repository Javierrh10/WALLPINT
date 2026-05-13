package com.raposo.wallpint.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raposo.wallpint.model.ClienteResumen

private val AzulOscuro = Color(0xFF002B5B)
private val AzulMedio = Color(0xFF0066CC)
private val AzulClaro = Color(0xFFD9F0FA)
private val FondoApp = Color(0xFFF4F7FB)
private val GrisTexto = Color(0xFF6B7280)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminClientesScreen(
    viewModel: GestionUsuariosViewModel,
    onAbrirCliente: (Long) -> Unit = {}
) {
    val clientes by viewModel.clientes.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()

    var consulta by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.cargarClientes() }

    val filtrados = remember(clientes, consulta) {
        if (consulta.isBlank()) clientes
        else clientes.filter {
            it.nombreCompleto.contains(consulta, true) ||
                    (it.email?.contains(consulta, true) == true) ||
                    (it.telefono?.contains(consulta) == true)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(FondoApp)) {
        // Buscador
        OutlinedTextField(
            value = consulta,
            onValueChange = { consulta = it },
            placeholder = { Text("Buscar por nombre, email o teléfono…") },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = GrisTexto) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        Text(
            "${filtrados.size} cliente${if (filtrados.size != 1) "s" else ""}",
            fontSize = 11.sp,
            color = GrisTexto,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
        )

        PullToRefreshBox(
            isRefreshing = cargando,
            onRefresh = { viewModel.cargarClientes() },
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                error != null -> Card(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(error!!, color = Color(0xFFB91C1C), modifier = Modifier.padding(16.dp), fontSize = 13.sp)
                }
                filtrados.isEmpty() && !cargando -> Box(
                    modifier = Modifier.fillMaxSize().padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Person, null, tint = Color.LightGray, modifier = Modifier.size(56.dp))
                        Spacer(Modifier.height(12.dp))
                        Text(
                            if (clientes.isEmpty()) "No hay clientes registrados"
                            else "Ningún cliente coincide con la búsqueda",
                            color = GrisTexto
                        )
                    }
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtrados, key = { it.id }) { c ->
                        ClienteCard(c, onClick = { onAbrirCliente(c.id) })
                    }
                    item { Spacer(Modifier.height(40.dp)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClienteCard(c: ClienteResumen, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(AzulClaro),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    c.nombre.firstOrNull()?.uppercase() ?: "?",
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(c.nombreCompleto, fontWeight = FontWeight.Bold, color = AzulOscuro)
                if (!c.email.isNullOrBlank()) {
                    Text(c.email, fontSize = 12.sp, color = GrisTexto)
                }
                if (!c.telefono.isNullOrBlank()) {
                    Text("Tel: ${c.telefono}", fontSize = 12.sp, color = GrisTexto)
                }
                if (!c.direccion.isNullOrBlank()) {
                    Text(c.direccion, fontSize = 12.sp, color = GrisTexto, maxLines = 2)
                }
            }
        }
    }
}
