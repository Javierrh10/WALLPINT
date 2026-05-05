package com.raposo.wallpint.ui.auth

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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

private val AzulOscuro = Color(0xFF003366)
private val AzulMedio = Color(0xFF0066CC)
private val GrisCampo = Color(0xFF8A9BB0)
private val Rojo = Color(0xFFB91C1C)

// ---------- Validadores ----------
private fun validarNombre(v: String): String? =
    if (v.isBlank()) "Obligatorio" else null

private fun validarEmail(v: String): String? = when {
    v.isBlank() -> "Obligatorio"
    !Patterns.EMAIL_ADDRESS.matcher(v).matches() -> "Formato de email inválido"
    else -> null
}

private fun validarTelefono(v: String): String? = when {
    v.isBlank() -> "Obligatorio"
    !v.all { it.isDigit() } -> "Solo números"
    v.length != 9 -> "Debe tener 9 dígitos"
    else -> null
}

private fun validarPassword(v: String): String? = when {
    v.isBlank() -> "Obligatorio"
    v.length < 6 -> "Mínimo 6 caracteres"
    else -> null
}

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

    // "touched": el campo solo muestra error tras la primera interacción del usuario.
    // Evita mostrar todos los errores en rojo al abrir la pantalla.
    var touchedNombre by remember { mutableStateOf(false) }
    var touchedApellidos by remember { mutableStateOf(false) }
    var touchedEmail by remember { mutableStateOf(false) }
    var touchedTelefono by remember { mutableStateOf(false) }
    var touchedPassword by remember { mutableStateOf(false) }

    var selectedRole by remember { mutableStateOf("CLIENTE") }
    var passwordVisible by remember { mutableStateOf(false) }

    val errorNombre = validarNombre(nombre)
    val errorApellidos = validarNombre(apellidos)
    val errorEmail = validarEmail(email)
    val errorTelefono = validarTelefono(telefono)
    val errorPassword = validarPassword(password)
    val formularioValido = listOf(errorNombre, errorApellidos, errorEmail, errorTelefono, errorPassword).all { it == null }

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
                    CampoConValidacion(
                        value = nombre,
                        onValueChange = { nombre = it; touchedNombre = true },
                        placeholder = "Nombre",
                        icon = Icons.Default.Person,
                        error = errorNombre,
                        mostrarError = touchedNombre
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Apellidos ──
                    CampoConValidacion(
                        value = apellidos,
                        onValueChange = { apellidos = it; touchedApellidos = true },
                        placeholder = "Apellidos",
                        icon = Icons.Default.Person,
                        error = errorApellidos,
                        mostrarError = touchedApellidos
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Teléfono ──
                    CampoConValidacion(
                        value = telefono,
                        onValueChange = { telefono = it.take(9); touchedTelefono = true },
                        placeholder = "Teléfono (9 dígitos)",
                        icon = Icons.Default.Phone,
                        error = errorTelefono,
                        mostrarError = touchedTelefono,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Email ──
                    CampoConValidacion(
                        value = email,
                        onValueChange = { email = it.trim(); touchedEmail = true },
                        placeholder = "correo@ejemplo.com",
                        icon = Icons.Default.Email,
                        error = errorEmail,
                        mostrarError = touchedEmail,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Contraseña ──
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; touchedPassword = true },
                        placeholder = { Text("Contraseña (mín. 6)", color = GrisCampo) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GrisCampo) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(Icons.Default.Lock, contentDescription = "Mostrar/Ocultar", tint = GrisCampo)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF8FAFC)),
                        singleLine = true,
                        isError = touchedPassword && errorPassword != null,
                        supportingText = {
                            if (touchedPassword && errorPassword != null) {
                                Text(errorPassword, color = Rojo, fontSize = 12.sp)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Error del backend (solo cuando llega) ──
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

                    // ── Botón Registrar ──
                    Button(
                        onClick = {
                            // Marcamos todos como touched para que se muestren errores si el usuario
                            // pulsa sin haber tocado los campos
                            touchedNombre = true; touchedApellidos = true; touchedEmail = true
                            touchedTelefono = true; touchedPassword = true
                            if (formularioValido) {
                                viewModel.register(nombre, apellidos, email, telefono, password, selectedRole)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulOscuro),
                        enabled = authState !is AuthState.Loading && formularioValido
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

@Composable
private fun CampoConValidacion(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    error: String?,
    mostrarError: Boolean,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = GrisCampo) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = GrisCampo) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF8FAFC)),
        singleLine = true,
        isError = mostrarError && error != null,
        supportingText = {
            if (mostrarError && error != null) {
                Text(error, color = Rojo, fontSize = 12.sp)
            }
        },
        keyboardOptions = keyboardOptions
    )
}
