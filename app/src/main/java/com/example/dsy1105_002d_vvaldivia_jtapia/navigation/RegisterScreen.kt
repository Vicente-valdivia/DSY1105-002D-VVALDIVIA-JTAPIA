package com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()
    val registroExitoso by viewModel.registroExitoso.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            Toast.makeText(context, "Registro exitoso, inicie sesión", Toast.LENGTH_LONG).show()
            onNavigateBack()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Registro de Usuario", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // Fila para Nombre y Apellido
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = formState.nombre,
                onValueChange = { viewModel.updateField("nombre", it) },
                label = { Text("Nombre") },
                isError = formState.errorNombre != null,
                supportingText = { formState.errorNombre?.let { Text(it) } },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = formState.apellido,
                onValueChange = { viewModel.updateField("apellido", it) },
                label = { Text("Apellido") },
                isError = formState.errorApellido != null,
                supportingText = { formState.errorApellido?.let { Text(it) } },
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = formState.email, onValueChange = { viewModel.updateField("email", it) },
            label = { Text("Correo Electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = formState.errorEmail != null,
            supportingText = { formState.errorEmail?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = formState.clave, onValueChange = { viewModel.updateField("clave", it) },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            isError = formState.errorClave != null,
            supportingText = { formState.errorClave?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = formState.confirmarClave, onValueChange = { viewModel.updateField("confirmar", it) },
            label = { Text("Confirmar Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            isError = formState.errorConfirmar != null,
            supportingText = { formState.errorConfirmar?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.registrar() }, modifier = Modifier.fillMaxWidth().height(50.dp)) {
            Text("Registrarse")
        }

        TextButton(onClick = onNavigateBack) {
            Text("Volver al Login")
        }
    }
}