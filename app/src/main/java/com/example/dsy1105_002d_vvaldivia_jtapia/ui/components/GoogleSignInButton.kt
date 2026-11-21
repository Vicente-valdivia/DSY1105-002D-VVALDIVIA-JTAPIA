package com.example.dsy1105_002d_vvaldivia_jtapia.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dsy1105_002d_vvaldivia_jtapia.R
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth.GoogleSignInResult
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth.requestGoogleIdToken
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.theme.MinimalSurfaceBorder
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.theme.MinimalTextPrimary
import kotlinx.coroutines.launch

/**
 * Botón "Continuar con Google": al pulsarlo abre el selector de cuentas de Google.
 * Usado tanto en Login como en Registro (con Google ambos son el mismo flujo).
 */
@Composable
fun GoogleSignInButton(
    enabled: Boolean,
    onIdToken: (String) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Continuar con Google"
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // default_web_client_id lo genera el plugin google-services desde google-services.json
    val serverClientId = stringResource(R.string.default_web_client_id)

    OutlinedButton(
        onClick = {
            scope.launch {
                when (val result = requestGoogleIdToken(context, serverClientId)) {
                    is GoogleSignInResult.Token -> onIdToken(result.idToken)
                    is GoogleSignInResult.Failure -> {
                        // Toast además del mensaje en pantalla, para que nunca pase desapercibido
                        Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                        onError(result.message)
                    }
                    GoogleSignInResult.Cancelled -> Unit
                }
            }
        },
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MinimalTextPrimary),
        border = BorderStroke(1.dp, MinimalSurfaceBorder),
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text(label, fontWeight = FontWeight.Medium, color = MinimalTextPrimary)
    }
}
