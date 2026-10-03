package com.example.dsy1105_002d_vvaldivia_jtapia.navigation

// Usamos sealed class para definir rutas seguras en la navegación[cite: 17]
sealed class Screen(val route: String) {
    data object Login : Screen("login_screen")
    data object Home : Screen("home_screen")
}