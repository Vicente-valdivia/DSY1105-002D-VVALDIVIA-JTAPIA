package com.example.dsy1105_002d_vvaldivia_jtapia.ui.navigation

sealed class AppScreen(val route: String) {
    object Login : AppScreen("login")
    object Register : AppScreen("register")
    object Main : AppScreen("main")
}