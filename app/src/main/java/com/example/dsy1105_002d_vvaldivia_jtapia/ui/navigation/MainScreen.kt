package com.example.dsy1105_002d_vvaldivia_jtapia.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.screens.*
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.theme.MinimalBackground
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.theme.MinimalPrimaryBlue
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.theme.MinimalSurface
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.theme.MinimalTextPrimary
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.theme.MinimalTextSecondary

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Home,
        Screen.Cameras,
        Screen.Events,
        Screen.Alerts,
        Screen.Profile
    )

    Scaffold(
        containerColor = MinimalBackground,
        bottomBar = {
            NavigationBar(
                containerColor = MinimalSurface,
                contentColor = MinimalTextPrimary
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = selected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MinimalTextPrimary,
                            selectedTextColor = MinimalTextPrimary,
                            unselectedIconColor = MinimalTextSecondary,
                            unselectedTextColor = MinimalTextSecondary,
                            indicatorColor = MinimalPrimaryBlue.copy(alpha = 0.4f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Cameras.route) { CamerasScreen() }
            composable(Screen.Events.route) { EventsScreen() }
            composable(Screen.Alerts.route) { AlertsScreen() }
            composable(Screen.Profile.route) { ProfileScreen(onLogout = onLogout) }
        }
    }
}