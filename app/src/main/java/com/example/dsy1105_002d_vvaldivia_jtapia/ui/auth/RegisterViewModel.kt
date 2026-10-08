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

data class RegisterFormState(
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val clave: String = "",
    val confirmarClave: String = "",
    val errorNombre: String? = null,
    val errorApellido: String? = null,
    val errorEmail: String? = null,
    val errorClave: String? = null,
    val errorConfirmar: String? = null
)

class RegisterViewModel : ViewModel() {

    private val _formState = MutableStateFlow(RegisterFormState())
    val formState: StateFlow<RegisterFormState> = _formState.asStateFlow()

    // Usamos un booleano reactivo para saber cuándo navegar hacia atrás
    private val _registroExitoso = MutableStateFlow(false)
    val registroExitoso: StateFlow<Boolean> = _registroExitoso.asStateFlow()

    fun updateField(field: String, value: String) {
        _formState.update {
            when (field) {
                "nombre" -> it.copy(nombre = value, errorNombre = null)
                "apellido" -> it.copy(apellido = value, errorApellido = null)
                "email" -> it.copy(email = value, errorEmail = null)
                "clave" -> it.copy(clave = value, errorClave = null)
                "confirmar" -> it.copy(confirmarClave = value, errorConfirmar = null)
                else -> it
            }
        }
    }

    fun registrar() {
        val form = _formState.value
        var hasError = false
        var errores = RegisterFormState(nombre = form.nombre, apellido = form.apellido, email = form.email, clave = form.clave, confirmarClave = form.confirmarClave)

        if (form.nombre.isBlank()) { errores = errores.copy(errorNombre = "Requerido"); hasError = true }
        if (form.apellido.isBlank()) { errores = errores.copy(errorApellido = "Requerido"); hasError = true }

        if (form.email.isBlank() || !form.email.contains("@")) {
            errores = errores.copy(errorEmail = "Correo inválido"); hasError = true
        } else if (UserRepository.emailExists(form.email.trim().lowercase())) {
            errores = errores.copy(errorEmail = "El correo ya está registrado"); hasError = true
        }

        if (form.clave.length < 6) {
            errores = errores.copy(errorClave = "Mínimo 6 caracteres"); hasError = true
        }
        if (form.clave != form.confirmarClave) {
            errores = errores.copy(errorConfirmar = "Las contraseñas no coinciden"); hasError = true
        }

        if (hasError) {
            _formState.value = errores
            return
        }

        viewModelScope.launch {
            UserRepository.registerUser(
                nombre = form.nombre.trim(),
                apellido = form.apellido.trim(),
                email = form.email.trim().lowercase(),
                clave = form.clave.trim()
            )
            _registroExitoso.value = true
        }
    }
}