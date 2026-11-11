package com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_002d_vvaldivia_jtapia.data.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginFormState(
    val email: String = "",
    val clave: String = "",
    val errorEmail: String? = null,
    val errorClave: String? = null,
    val mensajeGeneral: String? = null
)

class LoginViewModel : ViewModel() {

    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState.asStateFlow()

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _formState.update { it.copy(email = email, errorEmail = null, mensajeGeneral = null) }
    }

    fun onClaveChanged(clave: String) {
        _formState.update { it.copy(clave = clave, errorClave = null, mensajeGeneral = null) }
    }

    fun login() {
        val currentState = _formState.value
        var hasError = false

        val emailTrim = currentState.email.trim()
        val claveTrim = currentState.clave.trim()

        // Validación básica de formato de correo en cliente
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

        if (emailTrim.isBlank() || !emailTrim.matches(emailRegex)) {
            _formState.update { it.copy(errorEmail = "Ingrese un correo electrónico válido") }
            hasError = true
        }

        if (claveTrim.isBlank()) {
            _formState.update { it.copy(errorClave = "La contraseña no puede estar vacía") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            delay(600) // Simulación de latencia de red

            // Autenticación en el repositorio
            val user = UserRepository.authenticate(emailTrim, claveTrim)

            if (user != null) {
                // Éxito: Limpiamos formulario y pasamos al estado Success
                _formState.update { LoginFormState() }
                _uiState.value = LoginUiState.Success(user)
            } else {
                // FALLO DE SEGURIDAD: Se borra la contraseña y se muestra mensaje genérico
                _formState.update {
                    it.copy(
                        clave = "", // <-- Borra la contraseña obligatoriamente
                        mensajeGeneral = "Credenciales inválidas. Verifique su correo y clave."
                    )
                }
                _uiState.value = LoginUiState.Idle
            }
        }
    }
}