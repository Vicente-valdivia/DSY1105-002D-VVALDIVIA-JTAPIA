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
    // NavController es el controlador central que gestiona el backstack y la transición entre pantallas
    val navController = rememberNavController()
    // LocalContext permite acceder al contexto de Android para lanzar notificaciones como Toast
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // 1. Ruta de Inicio de Sesión
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { user ->
                    // Feedback visual al iniciar sesión correctamente
                    Toast.makeText(
                        context,
                        "Bienvenido ${user.name} (${user.role})",
                        Toast.LENGTH_LONG
                    ).show()

                    // Navegación segura hacia el Home
                    navController.navigate(Screen.Home.route) {
                        // popUpTo destruye la pantalla de Login del historial (Backstack).
                        // Esto evita que el usuario vuelva al login presionando el botón "Atrás" del celular.
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    // Navega a la pantalla de registro de forma estándar
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // 2. Ruta de Registro
        composable(route = Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    // popBackStack retira la pantalla actual (Registro) y vuelve a la anterior (Login)
                    navController.popBackStack()
                }
            )
        }

        // 3. Ruta del Panel Principal (Modo Guardián)
        composable(route = Screen.Home.route) {
            HomeScreen()
        }
    }
}