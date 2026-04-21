package com.raposo.wallpint.ui.auth

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
private val GrisCampo = Color(0xFF8A9BB0)

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateBackToLogin: () -> Unit
) {

    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var selectedRole by remember { mutableStateOf("CLIENTE") }

    var passwordVisible by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val authState = viewModel.authState
    val scrollState = rememberScrollState()

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE6F0FA), Color(0xFFC8E1F5))
    )

    LaunchedEffect(authState) {
        if (authState is AuthState.RegisterSuccess) {
            Toast.makeText(context, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            onNavigateBackToLogin()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(backgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            // Añadimos verticalScroll por si la pantalla del móvil es pequeña
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).verticalScroll(scrollState)
        ) {
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
                        text = "Crear Cuenta",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulOscuro,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // ── Selector de Rol ──
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Botón Cliente
                        OutlinedButton(
                            onClick = { selectedRole = "CLIENTE" },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (selectedRole == "CLIENTE") AzulOscuro.copy(alpha = 0.1f) else Color.Transparent,
                                contentColor = if (selectedRole == "CLIENTE") AzulOscuro else GrisCampo
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (selectedRole == "CLIENTE") AzulOscuro else GrisCampo.copy(alpha = 0.3f)
                            )
                        ) {
                            Text("Soy Cliente", fontWeight = if (selectedRole == "CLIENTE") FontWeight.Bold else FontWeight.Normal)
                        }

                        // Botón Pintor
                        OutlinedButton(
                            onClick = { selectedRole = "PINTOR" },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (selectedRole == "PINTOR") AzulOscuro.copy(alpha = 0.1f) else Color.Transparent,
                                contentColor = if (selectedRole == "PINTOR") AzulOscuro else GrisCampo
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (selectedRole == "PINTOR") AzulOscuro else GrisCampo.copy(alpha = 0.3f)
                            )
                        ) {
                            Text("Soy Pintor", fontWeight = if (selectedRole == "PINTOR") FontWeight.Bold else FontWeight.Normal)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ── Nombre ──
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        placeholder = { Text("Nombre", color = GrisCampo) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GrisCampo) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF8FAFC)),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Apellidos ──
                    OutlinedTextField(
                        value = apellidos,
                        onValueChange = { apellidos = it },
                        placeholder = { Text("Apellidos", color = GrisCampo) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GrisCampo) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF8FAFC)),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Teléfono ──
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        placeholder = { Text("Teléfono", color = GrisCampo) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GrisCampo) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF8FAFC)),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Email ──
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("correo@ejemplo.com", color = GrisCampo) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = GrisCampo) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF8FAFC)),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Contraseña ──
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GrisCampo) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(Icons.Default.Lock, contentDescription = "Mostrar", tint = GrisCampo)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF8FAFC)),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // ── Error de API ──
                    if (authState is AuthState.Error) {
                        Text(
                            text = authState.message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )
                    }

                    // ── Botón Registrar ──
                    Button(
                        // Pasamos los 5 datos al viewModel
                        onClick = { viewModel.register(nombre, apellidos, email, telefono, password, selectedRole) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulOscuro),
                        // El botón solo se activa si TODOS los campos tienen texto
                        enabled = authState !is AuthState.Loading && nombre.isNotBlank() && apellidos.isNotBlank() && email.isNotBlank() && telefono.isNotBlank() && password.isNotBlank()
                    ) {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Registrarse", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Volver al Login ──
            TextButton(onClick = {
                viewModel.resetState()
                onNavigateBackToLogin()
            }) {
                Text("¿Ya tienes cuenta? Inicia sesión", color = AzulOscuro, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}