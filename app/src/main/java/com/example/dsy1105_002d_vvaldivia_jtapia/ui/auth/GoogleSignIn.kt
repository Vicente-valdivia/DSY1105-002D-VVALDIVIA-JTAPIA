package com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.UserProfile
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.UserProfileFactory
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

sealed class GoogleSignInResult {
    data class Token(val idToken: String) : GoogleSignInResult()
    /** El usuario cerró el selector de cuentas: no es un error, no se muestra nada. */
    object Cancelled : GoogleSignInResult()
    data class Failure(val message: String) : GoogleSignInResult()
}

/**
 * Muestra el selector de cuentas de Google (el "Sign in with Google" estándar) y devuelve
 * el ID token. Usa GetSignInWithGoogleOption, pensado para un botón explícito: siempre abre
 * el selector, sin depender de cuentas "previamente autorizadas".
 *
 * [context] debe ser el contexto de una Activity (LocalContext en Compose lo es).
 */
suspend fun requestGoogleIdToken(context: Context, serverClientId: String): GoogleSignInResult {
    return try {
        val option = GetSignInWithGoogleOption.Builder(serverClientId).build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()

        val result = CredentialManager.create(context).getCredential(context, request)
        val credential = result.credential

        Log.d("GoogleSignIn", "Credencial recibida: ${credential::class.java.simpleName} type=${credential.type}")

        // El flujo "Sign in with Google" puede devolver TYPE_GOOGLE_ID_TOKEN_CREDENTIAL o su variante
        // TYPE_GOOGLE_ID_TOKEN_SIWG_CREDENTIAL; ambas se leen con GoogleIdTokenCredential.
        if (credential is CustomCredential &&
            credential.type.startsWith("com.google.android.libraries.identity.googleid.TYPE_GOOGLE_ID_TOKEN")
        ) {
            GoogleSignInResult.Token(GoogleIdTokenCredential.createFrom(credential.data).idToken)
        } else {
            GoogleSignInResult.Failure("Tipo de credencial no compatible con Google: ${credential.type}")
        }
    } catch (e: GetCredentialCancellationException) {
        // Android usa esta misma excepción cuando el usuario cierra el selector Y cuando Google rechaza
        // la app tras elegir cuenta (típico: SHA-1 de la máquina no registrado en Firebase). No se puede
        // distinguir, así que no se silencia.
        Log.e("GoogleSignIn", "Cancelado/rechazado: type=${e.type} msg=${e.message}", e)
        GoogleSignInResult.Failure(
            "El inicio con Google se canceló o fue rechazado. Si elegiste una cuenta y volvió aquí, " +
                "registra el SHA-1 de esta computadora en Firebase y descarga de nuevo google-services.json."
        )
    } catch (e: NoCredentialException) {
        GoogleSignInResult.Failure("No hay una cuenta de Google disponible en este dispositivo. Agrega una en Ajustes e inténtalo de nuevo.")
    } catch (e: GetCredentialException) {
        GoogleSignInResult.Failure("No se pudo iniciar con Google: ${e.message ?: e.type}")
    } catch (e: GoogleIdTokenParsingException) {
        GoogleSignInResult.Failure("Respuesta de Google inválida.")
    } catch (e: CancellationException) {
        throw e // nunca tragarse la cancelación de la corrutina
    } catch (e: Exception) {
        GoogleSignInResult.Failure("Error inesperado con Google: ${e.localizedMessage ?: "desconocido"}")
    }
}

/** Autentica en Firebase con el ID token de Google (crea la cuenta si no existía). */
suspend fun FirebaseAuth.signInWithGoogleIdToken(idToken: String): UserProfile? {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    val user = signInWithCredential(credential).await().user ?: return null
    return UserProfileFactory.create(user.displayName, user.email ?: "")
}
