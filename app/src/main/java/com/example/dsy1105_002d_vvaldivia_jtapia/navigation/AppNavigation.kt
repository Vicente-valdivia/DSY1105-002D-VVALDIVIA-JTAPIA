package com.example.dsy1105_002d_vvaldivia_jtapia.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth.LoginScreen
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.home.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Obtenemos el contexto actual para poder lanzar el Toast
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // Ruta 1: Pantalla de Login
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { user ->
                    // 1. Mostramos el mensaje de éxito nuevamente
                    Toast.makeText(
                        context, 
                        "Bienvenido ${user.name} (${user.role})", 
                        Toast.LENGTH_LONG
                    ).show()

                    // 2. Navegamos al Home y destruimos el login del historial
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Ruta 2: Pantalla Principal (Home)
        composable(route = Screen.Home.route) {
            HomeScreen()
        }
    }
}
