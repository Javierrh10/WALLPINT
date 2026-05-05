package com.raposo.wallpint.ui.auth

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions

private val AzulOscuro = Color(0xFF003366)
private val AzulMedio = Color(0xFF0066CC)
private val GrisTexto = Color(0xFF666666)
private val GrisCampo = Color(0xFF8A9BB0)
private val Rojo = Color(0xFFB91C1C)

private fun validarEmailLogin(v: String): String? = when {
    v.isBlank() -> "Introduce tu email"
    !Patterns.EMAIL_ADDRESS.matcher(v).matches() -> "Email no válido"
    else -> null
}

private fun validarPasswordLogin(v: String): String? = when {
    v.isBlank() -> "Introduce tu contraseña"
    else -> null
}

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var touchedEmail by remember { mutableStateOf(false) }
    var touchedPassword by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val authState = viewModel.authState

    val errorEmail = validarEmailLogin(email)
    val errorPassword = validarPasswordLogin(password)
    val formValido = errorEmail == null && errorPassword == null

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE6F0FA), Color(0xFFC8E1F5))
    )

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess(authState.rol, authState.nombreUsuario)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(backgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
        ) {
            // ── Logo ──
            Box(
                modifier = Modifier.size(80.dp).background(AzulOscuro, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) { Text("🖌️", fontSize = 36.sp) }

            Spacer(modifier = Modifier.height(16.dp))

            Text("WallPint", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
            Text(
                "GESTIÓN DE PINTURA PROFESIONAL",
                fontSize = 12.sp, color = GrisTexto, letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

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
                        "Bienvenido",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulOscuro,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // ── Email ──
                    Text(
                        "CORREO ELECTRÓNICO",
                        fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                        color = GrisCampo, letterSpacing = 1.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it.trim(); touchedEmail = true },
                        placeholder = { Text("nombre@ejemplo.com", color = GrisCampo) },
                        trailingIcon = { Icon(Icons.Default.Email, null, tint = GrisCampo) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE0E8F0),
                            focusedBorderColor = AzulMedio,
                            unfocusedContainerColor = Color(0xFFF8FAFC),
                            focusedContainerColor = Color.White
                        ),
                        singleLine = true,
                        isError = touchedEmail && errorEmail != null,
                        supportingText = {
                            if (touchedEmail && errorEmail != null) {
                                Text(errorEmail, color = Rojo, fontSize = 12.sp)
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // ── Contraseña ──
                    Text(
                        "CONTRASEÑA",
                        fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                        color = GrisCampo, letterSpacing = 1.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; touchedPassword = true },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(Icons.Default.Lock, "Mostrar/ocultar contraseña", tint = GrisCampo)
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
                        singleLine = true,
                        isError = touchedPassword && errorPassword != null,
                        supportingText = {
                            if (touchedPassword && errorPassword != null) {
                                Text(errorPassword, color = Rojo, fontSize = 12.sp)
                            }
                        }
                    )

                    // ── ¿Olvidaste contraseña? (placeholder) ──
                    TextButton(
                        onClick = {
                            Toast.makeText(
                                context,
                                "Función disponible próximamente. Contacta con soporte.",
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("¿Olvidaste tu contraseña?", color = AzulMedio, fontSize = 13.sp)
                    }

                    // ── Card de error de API ──
                    if (authState is AuthState.Error) {
                        Surface(
                            color = Rojo.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, null, tint = Rojo, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(authState.message, color = Rojo, fontSize = 13.sp)
                            }
                        }
                    }

                    // ── Botón Iniciar sesión ──
                    Button(
                        onClick = {
                            touchedEmail = true; touchedPassword = true
                            if (formValido) viewModel.login(email, password)
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulOscuro),
                        enabled = authState !is AuthState.Loading && formValido
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Iniciar sesión →", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Crear cuenta ──
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿Nuevo cliente? ", color = GrisTexto, fontSize = 14.sp)
                TextButton(onClick = onNavigateToRegister) {
                    Text("Crear cuenta", color = AzulOscuro, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
