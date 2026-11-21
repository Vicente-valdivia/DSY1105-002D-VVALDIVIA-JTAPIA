package com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth

import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.TimeoutCancellationException

/** Tiempo máximo de espera de Firebase para no dejar el botón cargando para siempre. */
const val AUTH_TIMEOUT_MS = 20_000L

/** Traduce cualquier error de autenticación a un mensaje útil (y lo deja en Logcat con tag "AuthError"). */
fun Throwable.toAuthMessage(): String {
    Log.e("AuthError", "Fallo de autenticación: ${this::class.java.simpleName}: $message", this)

    val code = (this as? FirebaseAuthException)?.errorCode.orEmpty()
    val raw = message.orEmpty()

    return when {
        this is TimeoutCancellationException ->
            "Firebase no respondió a tiempo. Revisa tu conexión (en emulador, que tenga Google Play) e inténtalo de nuevo."
        this is FirebaseNetworkException ->
            "Sin conexión. Revisa tu internet e inténtalo de nuevo."
        this is FirebaseTooManyRequestsException ->
            "Demasiados intentos. Espera unos minutos e inténtalo de nuevo."
        this is FirebaseAuthUserCollisionException ->
            "Este correo ya está registrado."
        this is FirebaseAuthWeakPasswordException ->
            "La contraseña es demasiado débil."
        this is FirebaseAuthInvalidUserException || this is FirebaseAuthInvalidCredentialsException ->
            "Correo o contraseña incorrectos."
        code == "ERROR_OPERATION_NOT_ALLOWED" ||
            raw.contains("CONFIGURATION_NOT_FOUND") || raw.contains("OPERATION_NOT_ALLOWED") ->
            "El acceso con correo y contraseña no está habilitado en Firebase (Authentication > Método de acceso > Correo/contraseña)."
        else -> "Error de autenticación (${code.ifBlank { this::class.java.simpleName }}): $raw"
    }
}
