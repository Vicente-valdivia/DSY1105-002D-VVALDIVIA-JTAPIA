package com.example.dsy1105_002d_vvaldivia_jtapia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth.LoginUiState
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.auth.LoginViewModel
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Modo Guardián",
            color = MinimalTextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Acceso a Central de Monitoreo",
            color = MinimalTextSecondary,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MinimalSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = formState.email,
                    onValueChange = { viewModel.onEmailChanged(it) },
                    label = { Text("Correo Electrónico", color = MinimalTextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MinimalPrimaryBlue) },
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
                    label = { Text("Contraseña", color = MinimalTextSecondary) },
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

                if (formState.mensajeGeneral != null) {
                    Surface(
                        color = MinimalAccentRed.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = formState.mensajeGeneral!!,
                            color = MinimalAccentRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Button(
                    onClick = { viewModel.login() },
                    enabled = uiState !is LoginUiState.Loading,
                    colors = ButtonDefaults.buttonColors(containerColor = MinimalPrimaryBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (uiState is LoginUiState.Loading) {
                        CircularProgressIndicator(color = MinimalTextPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Iniciar Sesión", color = MinimalTextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        TextButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("¿No tienes cuenta? Regístrate aquí", color = MinimalPrimaryBlue)
        }
    }
}