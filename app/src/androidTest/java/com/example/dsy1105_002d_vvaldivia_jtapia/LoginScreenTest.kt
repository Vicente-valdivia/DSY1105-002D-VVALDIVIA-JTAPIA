package com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.screens.LoginScreen
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun el_titulo_y_subtitulo_deben_aparecer_en_pantalla() {
        composeRule.setContent {
            LoginScreen(
                onLoginSuccess = {},
                onNavigateToRegister = {}
            )
        }

        composeRule.onNodeWithText("Modo Guardián").assertIsDisplayed()
        composeRule.onNodeWithText("Monitoreo móvil de eventos").assertIsDisplayed()
        composeRule.onNodeWithText("Iniciar Sesión").assertIsDisplayed()
    }
}