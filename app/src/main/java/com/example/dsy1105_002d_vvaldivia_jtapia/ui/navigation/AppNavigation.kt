package com.example.dsy1105_002d_vvaldivia_jtapia.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.UserProfileFactory
import com.example.dsy1105_002d_vvaldivia_jtapia.data.repository.SecurityRepository
import com.google.firebase.auth.FirebaseAuth
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
                },
                onGoogleSuccess = {
                    // Con Google registrarse = iniciar sesión: vamos directo a la app
                    navController.navigate(AppScreen.Main.route) {
                        popUpTo(AppScreen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppScreen.Main.route) {
            // Si el proceso fue recreado, el perfil en memoria se pierde aunque Firebase conserve la
            // sesión: lo reconstruimos para no mostrar la app con datos en blanco.
            LaunchedEffect(Unit) {
                if (SecurityRepository.currentUser.email.isBlank()) {
                    FirebaseAuth.getInstance().currentUser?.let { user ->
                        SecurityRepository.currentUser =
                            UserProfileFactory.create(user.displayName, user.email ?: "")
                    }
                }
            }
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