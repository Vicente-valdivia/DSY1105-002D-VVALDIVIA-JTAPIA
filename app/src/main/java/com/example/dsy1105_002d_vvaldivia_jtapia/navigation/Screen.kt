package com.example.dsy1105_002d_vvaldivia_jtapia.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login_screen")
    data object Home : Screen("home_screen")
    data object Register : Screen("register_screen") // Nueva ruta
}