package com.example.dsy1105_002d_vvaldivia_jtapia

import com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth.LoginUiState
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth.LoginViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest : StringSpec({

    "viewModel debe actualizar estado a Success cuando las credenciales son válidas" {
        // Configuramos el dispatcher virtual para pruebas de corrutinas
        val testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        try {
            val viewModel = LoginViewModel()

            viewModel.onEmailChange("admin@modoguardian.cl")
            viewModel.onPasswordChange("1234")

            runTest {
                viewModel.login()
                // Avanzamos el tiempo virtual de la corrutina para saltar el delay(1000)
                testDispatcher.scheduler.advanceUntilIdle()

                val currentState = viewModel.uiState.value
                (currentState is LoginUiState.Success).shouldBe(true)

                val successState = currentState as LoginUiState.Success
                successState.user.email shouldBe "admin@modoguardian.cl"
            }
        } finally {
            // Limpiamos el hilo principal al terminar la prueba
            Dispatchers.resetMain()
        }
    }
})