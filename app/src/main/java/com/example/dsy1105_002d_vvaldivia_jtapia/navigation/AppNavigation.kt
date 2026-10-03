package com.example.dsy1105_002d_vvaldivia_jtapia.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth.LoginScreen
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.home.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // Ruta 1: Pantalla de Login
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { user ->
                    // Cuando el login es exitoso, navegamos al Home
                    navController.navigate(Screen.Home.route) {
                        // BUENA PRÁCTICA: Destruimos la pantalla de Login del historial
                        // para que si el usuario presiona "Atrás" en su teléfono,
                        // salga de la app en lugar de volver a ver el login.
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