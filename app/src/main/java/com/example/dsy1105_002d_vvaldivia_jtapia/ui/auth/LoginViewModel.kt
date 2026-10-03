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

// 1. Data class para el formulario (alineado a la Guía 11)
data class LoginFormState(
    val email: String = "",
    val clave: String = "",
    val errorEmail: String? = null,
    val errorClave: String? = null
)

class LoginViewModel : ViewModel() {

    // Estado del formulario encapsulado
    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState.asStateFlow()

    // Estado de la pantalla (Loading, Success, Error)
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
        val email = currentForm.email.trim()
        val password = currentForm.clave.trim()

        var hasError = false

        // 1. Validación del Email
        if (email.isBlank()) {
            _formState.update { it.copy(errorEmail = "El correo es obligatorio") }
            hasError = true
        } else {
            _formState.update { it.copy(errorEmail = null) }
        }

        // 2. Validación de la Contraseña
        if (password.isBlank()) {
            _formState.update { it.copy(errorClave = "La contraseña es obligatoria") }
            hasError = true
        } else if (password != "1234") {
            _formState.update { it.copy(errorClave = "Contraseña incorrecta") }
            hasError = true
        } else {
            _formState.update { it.copy(errorClave = null) }
        }

        // Si hay errores, detenemos el flujo de login
        if (hasError) return

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            // Simulación de respuesta de API REST
            delay(1500)

            val user = when {
                email.contains("admin", ignoreCase = true) -> User(
                    id = "1",
                    name = "Admin General",
                    email = email,
                    role = UserRole.ADMINISTRADOR
                )
                email.contains("super", ignoreCase = true) -> User(
                    id = "2",
                    name = "Supervisor de Turno",
                    email = email,
                    role = UserRole.SUPERVISOR
                )
                else -> User(
                    id = "3",
                    name = "Operador de Cámaras",
                    email = email,
                    role = UserRole.OPERADOR
                )
            }

            _uiState.value = LoginUiState.Success(user)
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
        _formState.update { LoginFormState() }
    }
}