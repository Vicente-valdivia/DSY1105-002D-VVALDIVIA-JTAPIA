package com.example.dsy1105_002d_vvaldivia_jtapia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dsy1105_002d_vvaldivia_jtapia.data.repository.SecurityRepository
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.theme.*

@Composable
fun ProfileScreen() {
    val user = SecurityRepository.currentUser

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "Mi Perfil", color = MinimalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(text = "Cuenta y preferencias del turno", color = MinimalTextSecondary, fontSize = 12.sp)
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MinimalSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MinimalSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MinimalPrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = user.initials, color = MinimalTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = user.fullName, color = MinimalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = user.email, color = MinimalTextSecondary, fontSize = 12.sp)
                        Surface(color = MinimalAccentGreen.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                            Text(text = "Correo Verificado", color = MinimalAccentGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MinimalSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MinimalSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Información del Turno", color = MinimalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Horario: ${user.shiftTime}", color = MinimalTextSecondary, fontSize = 12.sp)
                        Text(text = "Cámaras: ${user.totalCameras}", color = MinimalTextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = MinimalAccentRed.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Cerrar Sesión", color = MinimalAccentRed, fontWeight = FontWeight.Bold)
            }
        }
    }
}