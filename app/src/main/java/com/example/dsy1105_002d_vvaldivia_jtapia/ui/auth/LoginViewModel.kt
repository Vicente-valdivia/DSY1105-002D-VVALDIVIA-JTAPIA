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

// Estado de los campos y errores del formulario
data class LoginFormState(
    val email: String = "",
    val clave: String = "",
    val errorEmail: String? = null,
    val errorClave: String? = null
)

class LoginViewModel : ViewModel() {

    // Lista de correos autorizados en el sistema
    private val validEmails = setOf(
        "admin@guardian.test",
        "supervisor@guardian.test",
        "operador@guardian.test"
    )

    // Regex para validar formato de correo electrónico
    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow = _formState.asStateFlow()

    private val _uiState = MutableStateFlow(LoginUiState.Idle)
    val uiState: StateFlow = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _formState.update { it.copy(email = newEmail, errorEmail = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _formState.update { it.copy(clave = newPassword, errorClave = null) }
    }

    fun login() {
        val currentForm = _formState.value
        val email = currentForm.email.trim().lowercase()
        val password = currentForm.clave.trim()

        // 1. Validación del Email
        val emailError = when {
            email.isBlank() -> "El correo es obligatorio"
            !email.matches(emailRegex) -> "Formato de correo inválido (ej. usuario@guardian.test)"
            email not in validEmails -> "El correo no corresponde a un usuario registrado"
            else -> null
        }

        // 2. Validación de la Contraseña
        val passwordError = when {
            password.isBlank() -> "La contraseña es obligatoria"
            password != "123456" -> "Contraseña incorrecta"
            else -> null
        }

        // Actualizamos los mensajes de error en el estado del formulario
        _formState.update {
            it.copy(
                errorEmail = emailError,
                errorClave = passwordError
            )
        }

        // Si existe algún error de validación, detenemos el proceso
        if (emailError != null || passwordError != null) return

        // 3. Proceso de Inicio de Sesión
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            // Simulación de respuesta de red / API
            delay(1500)

            val user = when (email) {
                "admin@guardian.test" -> User(
                    id = "1",
                    name = "Admin General",
                    email = email,
                    role = UserRole.ADMINISTRADOR
                )
                "supervisor@guardian.test" -> User(
                    id = "2",
                    name = "Supervisor de Turno",
                    email = email,
                    role = UserRole.SUPERVISOR
                )
                "operador@guardian.test" -> User(
                    id = "3",
                    name = "Operador de Cámaras",
                    email = email,
                    role = UserRole.OPERADOR
                )
                else -> {
                    _uiState.value = LoginUiState.Error("Usuario no encontrado")
                    return@launch
                }
            }

            _uiState.value = LoginUiState.Success(user)
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
        _formState.update { LoginFormState() }
    }
}
