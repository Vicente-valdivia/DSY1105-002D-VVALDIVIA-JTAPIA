package com.example.dsy1105_002d_vvaldivia_jtapia.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth.LoginScreen
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth.RegisterScreen
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.home.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // 1. Ruta de Inicio de Sesión
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { user ->
                    Toast.makeText(
                        context,
                        "Bienvenido ${user.name} (${user.role})",
                        Toast.LENGTH_LONG
                    ).show()

                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // 2. Ruta de Registro
        composable(route = Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 3. Ruta del Panel Principal (Modo Guardián) con Cierre de Sesión
        composable(route = Screen.Home.route) {
            HomeScreen(
                onLogout = {
                    Toast.makeText(context, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()

                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
