package com.example.dsy1105_002d_vvaldivia_jtapia.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.screens.LoginScreen
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.screens.RegisterScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreen.Login.route
    ) {
        composable(AppScreen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppScreen.Main.route) {
                        popUpTo(AppScreen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AppScreen.Register.route)
                }
            )
        }

        composable(AppScreen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    // Al registrarse exitosamente, vuelve al Login para obligar a ingresar contraseña
                    navController.popBackStack()
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppScreen.Main.route) {
            MainScreen(
                onLogout = {
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(AppScreen.Main.route) { inclusive = true }
                    }
                }
            )
        }
    }
}