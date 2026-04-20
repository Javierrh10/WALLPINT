package com.raposo.wallpint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.raposo.wallpint.data.preferences.TokenManager
import com.raposo.wallpint.ui.auth.AuthViewModel
import com.raposo.wallpint.ui.auth.LoginScreen
import com.raposo.wallpint.ui.theme.WallPintTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializamos lo que necesita el ViewModel
        val tokenManager = TokenManager(this)
        val viewModel = AuthViewModel(tokenManager)

        setContent {
            WallPintTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onNavigateToRegister = {

                    }
                )
            }
        }
    }
}