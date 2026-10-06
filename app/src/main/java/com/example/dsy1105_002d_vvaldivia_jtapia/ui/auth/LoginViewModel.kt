package com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.User
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.UserRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. Data class para el formulario..
data class LoginFormState(
    val email: String = "",
    val clave: String = "",
    val errorEmail: String? = null,
    val errorClave: String? = null
)

class LoginViewModel : ViewModel() {

    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState.asStateFlow()

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _formState.update { it.copy(email = newEmail, errorEmail = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _formState.update { it.copy(clave = newPassword, errorClave = null) }
    }

    fun login() {
        val currentForm = _formState.value
        val emailInput = currentForm.email.trim().lowercase()
        val password = currentForm.clave.trim()

        var hasError = false
        var errorMsgEmail: String? = null
        var errorMsgClave: String? = null

        val correosValidos = listOf(
            "admin@modoguardian.cl",
            "super@modoguardian.cl",
            "operador@modoguardian.cl"
        )

        // 1. Validación de campos vacíos
        if (emailInput.isBlank() || password.isBlank()) {
            if (emailInput.isBlank()) errorMsgEmail = "El correo es obligatorio"
            if (password.isBlank()) errorMsgClave = "La contraseña es obligatoria"
            hasError = true
        }
        // 2. Validación estricta y segura (Anti-enumeración)
        // Si no están vacíos, pero cualquiera de los dos es incorrecto:
        else if (emailInput !in correosValidos || password != "1234") {
            // Asignamos el mismo mensaje de error a AMBOS campos para mantener
            // la consistencia visual que pediste y mejorar la seguridad.
            errorMsgEmail = "Credenciales incorrectas"
            errorMsgClave = "Credenciales incorrectas"
            hasError = true
        }

        // 3. Limpieza de campos reactiva
        // Borramos el texto digitado, pero conservamos los mensajes de error
        _formState.update {
            it.copy(
                clave = "",
                errorEmail = errorMsgEmail,
                errorClave = errorMsgClave
            )
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            delay(1500)

            val user = when (emailInput) {
                "admin@modoguardian.cl" -> User("1", "Admin General", emailInput, UserRole.ADMINISTRADOR)
                "super@modoguardian.cl" -> User("2", "Supervisor de Turno", emailInput, UserRole.SUPERVISOR)
                else -> User("3", "Operador de Cámaras", emailInput, UserRole.OPERADOR)
            }

            _uiState.value = LoginUiState.Success(user)
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
        _formState.update { LoginFormState() }
    }
}
