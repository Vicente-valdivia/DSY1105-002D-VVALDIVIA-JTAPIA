package com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.UserProfileFactory
import com.example.dsy1105_002d_vvaldivia_jtapia.data.repository.SecurityRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

    // lazy: no toca Firebase hasta que realmente se autentica (permite probar la validación sin Firebase)
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    fun onEmailChanged(email: String) {
        _formState.update { it.copy(email = email, errorEmail = null, mensajeGeneral = null) }
    }

    fun onClaveChanged(clave: String) {
        _formState.update { it.copy(clave = clave, errorClave = null, mensajeGeneral = null) }
    }

    // 1. Inicio de sesión con correo y contraseña en Firebase Auth
    fun login() {
        if (_uiState.value is LoginUiState.Loading) return // evita doble envío

        val currentState = _formState.value
        var hasError = false

        val emailTrim = currentState.email.trim()
        val claveTrim = currentState.clave.trim()

        if (emailTrim.isBlank() || !emailTrim.matches(EMAIL_REGEX)) {
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
            try {
                val firebaseUser = withTimeout(AUTH_TIMEOUT_MS) {
                    firebaseAuth.signInWithEmailAndPassword(emailTrim, claveTrim).await()
                }.user

                if (firebaseUser != null) {
                    completeLogin(UserProfileFactory.create(firebaseUser.displayName, firebaseUser.email ?: emailTrim))
                } else {
                    showError("No se pudo obtener la sesión del usuario.")
                }
            } catch (e: TimeoutCancellationException) {
                showError(e.toAuthMessage())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                showError(e.toAuthMessage())
            }
        }
    }

    // 2. Inicio de sesión / registro con Google en Firebase Auth
    fun signInWithGoogle(idToken: String) {
        if (_uiState.value is LoginUiState.Loading) return

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val profile = withTimeout(AUTH_TIMEOUT_MS) { firebaseAuth.signInWithGoogleIdToken(idToken) }
                if (profile != null) {
                    completeLogin(profile)
                } else {
                    showError("No se pudo obtener el usuario de Firebase.")
                }
            } catch (e: TimeoutCancellationException) {
                showError(e.toAuthMessage())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                showError(e.toAuthMessage())
            }
        }
    }

    /** Errores del selector de Google (cuenta no disponible, etc.). */
    fun onGoogleError(message: String) {
        _formState.update { it.copy(mensajeGeneral = message) }
    }

    private fun completeLogin(profile: com.example.dsy1105_002d_vvaldivia_jtapia.data.model.UserProfile) {
        SecurityRepository.currentUser = profile
        _formState.update { LoginFormState() }
        _uiState.value = LoginUiState.Success(profile)
    }

    private fun showError(message: String) {
        _uiState.value = LoginUiState.Idle
        _formState.update { it.copy(mensajeGeneral = message) }
    }

    private companion object {
        val EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    }
}
