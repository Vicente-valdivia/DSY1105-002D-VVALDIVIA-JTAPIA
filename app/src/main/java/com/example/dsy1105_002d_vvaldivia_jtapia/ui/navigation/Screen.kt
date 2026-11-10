package com.example.dsy1105_002d_vvaldivia_jtapia.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Inicio", Icons.Default.Home)
    object Cameras : Screen("cameras", "Cámaras", Icons.Default.Videocam)
    object Events : Screen("events", "Registros", Icons.Default.ViewList)
    object Alerts : Screen("alerts", "Alertas", Icons.Default.Notifications)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)
}