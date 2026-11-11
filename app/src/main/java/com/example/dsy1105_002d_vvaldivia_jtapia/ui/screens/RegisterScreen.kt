package com.example.dsy1105_002d_vvaldivia_jtapia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth.RegisterViewModel
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.theme.*

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(formState.registroExitoso) {
        if (formState.registroExitoso) {
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalBackground)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Registro de Operador",
            color = MinimalTextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Credenciales para Modo Guardián",
            color = MinimalTextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MinimalSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = formState.nombre,
                    onValueChange = { viewModel.onNombreChanged(it) },
                    label = { Text("Nombre", color = MinimalTextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MinimalPrimaryBlue) },
                    isError = formState.errorNombre != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MinimalPrimaryBlue,
                        unfocusedBorderColor = MinimalSurfaceBorder,
                        focusedTextColor = MinimalTextPrimary,
                        unfocusedTextColor = MinimalTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (formState.errorNombre != null) {
                    Text(text = formState.errorNombre!!, color = MinimalAccentRed, fontSize = 11.sp)
                }

                OutlinedTextField(
                    value = formState.apellido,
                    onValueChange = { viewModel.onApellidoChanged(it) },
                    label = { Text("Apellido", color = MinimalTextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MinimalPrimaryBlue) },
                    isError = formState.errorApellido != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MinimalPrimaryBlue,
                        unfocusedBorderColor = MinimalSurfaceBorder,
                        focusedTextColor = MinimalTextPrimary,
                        unfocusedTextColor = MinimalTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (formState.errorApellido != null) {
                    Text(text = formState.errorApellido!!, color = MinimalAccentRed, fontSize = 11.sp)
                }

                OutlinedTextField(
                    value = formState.email,
                    onValueChange = { viewModel.onEmailChanged(it) },
                    label = { Text("Correo Electrónico (Estándar)", color = MinimalTextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MinimalPrimaryBlue) },
                    isError = formState.errorEmail != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MinimalPrimaryBlue,
                        unfocusedBorderColor = MinimalSurfaceBorder,
                        focusedTextColor = MinimalTextPrimary,
                        unfocusedTextColor = MinimalTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (formState.errorEmail != null) {
                    Text(text = formState.errorEmail!!, color = MinimalAccentRed, fontSize = 11.sp)
                }

                OutlinedTextField(
                    value = formState.clave,
                    onValueChange = { viewModel.onClaveChanged(it) },
                    label = { Text("Contraseña (Mín. 6 caracteres)", color = MinimalTextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MinimalPrimaryBlue) },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = formState.errorClave != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MinimalPrimaryBlue,
                        unfocusedBorderColor = MinimalSurfaceBorder,
                        focusedTextColor = MinimalTextPrimary,
                        unfocusedTextColor = MinimalTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (formState.errorClave != null) {
                    Text(text = formState.errorClave!!, color = MinimalAccentRed, fontSize = 11.sp)
                }

                OutlinedTextField(
                    value = formState.confirmarClave,
                    onValueChange = { viewModel.onConfirmarClaveChanged(it) },
                    label = { Text("Confirmar Contraseña", color = MinimalTextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MinimalPrimaryBlue) },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = formState.errorConfirmar != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MinimalPrimaryBlue,
                        unfocusedBorderColor = MinimalSurfaceBorder,
                        focusedTextColor = MinimalTextPrimary,
                        unfocusedTextColor = MinimalTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (formState.errorConfirmar != null) {
                    Text(text = formState.errorConfirmar!!, color = MinimalAccentRed, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.registrar() },
                    enabled = !formState.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = MinimalPrimaryBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (formState.isLoading) {
                        CircularProgressIndicator(color = MinimalTextPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Registrar Credencial", color = MinimalTextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        TextButton(
            onClick = onBackToLogin,
            modifier = Modifier.padding(top = 12.dp, bottom = 20.dp)
        ) {
            Text("¿Ya tienes cuenta? Inicia sesión", color = MinimalPrimaryBlue)
        }
    }
}