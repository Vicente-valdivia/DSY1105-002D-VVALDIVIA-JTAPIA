package com.example.dsy1105_002d_vvaldivia_jtapia

import com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth.LoginUiState
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth.LoginViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

// Solo se prueba la validación local: no requiere Firebase (el ViewModel lo crea de forma perezosa).
class LoginViewModelTest : StringSpec({

    "un correo inválido marca error y no inicia la autenticación" {
        val viewModel = LoginViewModel()
        viewModel.onEmailChanged("no-es-un-correo")
        viewModel.onClaveChanged("123456")

        viewModel.login()

        viewModel.formState.value.errorEmail.shouldNotBeNull()
        viewModel.uiState.value shouldBe LoginUiState.Idle
    }

    "una contraseña vacía marca error" {
        val viewModel = LoginViewModel()
        viewModel.onEmailChanged("guardian@insercode.cl")
        viewModel.onClaveChanged("   ")

        viewModel.login()

        viewModel.formState.value.errorClave.shouldNotBeNull()
        viewModel.formState.value.errorEmail.shouldBeNull()
        viewModel.uiState.value shouldBe LoginUiState.Idle
    }

    "editar un campo limpia su error" {
        val viewModel = LoginViewModel()
        viewModel.login()
        viewModel.formState.value.errorEmail.shouldNotBeNull()

        viewModel.onEmailChanged("a")

        viewModel.formState.value.errorEmail.shouldBeNull()
    }

    "un error del selector de Google se muestra como mensaje general" {
        val viewModel = LoginViewModel()
        viewModel.onGoogleError("Sin cuentas")
        viewModel.formState.value.mensajeGeneral shouldBe "Sin cuentas"
    }
})
