package com.raposo.wallpint.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AzulOscuro = Color(0xFF003366)
private val AzulMedio = Color(0xFF0066CC)
private val GrisTexto = Color(0xFF666666)
private val GrisCampo = Color(0xFF8A9BB0)

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val authState = viewModel.authState

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE6F0FA), Color(0xFFC8E1F5))
    )

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            val rol = authState.rol
            onLoginSuccess(rol)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {

            // ── Logo ──
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(AzulOscuro, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🖌️", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "WallPint",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro
            )
            Text(
                text = "GESTIÓN DE PINTURA PROFESIONAL",
                fontSize = 12.sp,
                color = GrisTexto,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Card ──
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Bienvenido",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulOscuro,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // ── Email ──
                    Text(
                        "CORREO ELECTRÓNICO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GrisCampo,
                        letterSpacing = 1.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("nombre@ejemplo.com", color = GrisCampo) },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = GrisCampo
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE0E8F0),
                            focusedBorderColor = AzulMedio,
                            unfocusedContainerColor = Color(0xFFF8FAFC),
                            focusedContainerColor = Color.White
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Contraseña ──
                    Text(
                        "CONTRASEÑA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GrisCampo,
                        letterSpacing = 1.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Lock else Icons.Default.Lock,
                                    contentDescription = "Mostrar contraseña",
                                    tint = GrisCampo
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE0E8F0),
                            focusedBorderColor = AzulMedio,
                            unfocusedContainerColor = Color(0xFFF8FAFC),
                            focusedContainerColor = Color.White
                        ),
                        singleLine = true
                    )

                    // ── ¿Olvidaste contraseña? ──
                    TextButton(
                        onClick = { /* TODO */ },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("¿Olvidaste tu contraseña?", color = AzulMedio, fontSize = 13.sp)
                    }

                    // ── Error de API ──
                    if (authState is AuthState.Error) {
                        Text(
                            text = authState.message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                    }

                    // ── Botón ──
                    Button(
                        onClick = { viewModel.login(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulOscuro),
                        enabled = authState !is AuthState.Loading
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Iniciar sesión →",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Biometría / QR ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AzulOscuro)
                ) {
                    Text("🔏  Biometría")
                }
                OutlinedButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AzulOscuro)
                ) {
                    Text("⬛  Código QR")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Crear cuenta ──
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿Nuevo cliente? ", color = GrisTexto, fontSize = 14.sp)
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        "Crear cuenta",
                        color = AzulOscuro,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}