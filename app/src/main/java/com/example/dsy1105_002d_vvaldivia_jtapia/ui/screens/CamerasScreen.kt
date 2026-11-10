package com.example.dsy1105_002d_vvaldivia_jtapia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dsy1105_002d_vvaldivia_jtapia.data.repository.SecurityRepository
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.theme.*

@Composable
fun CamerasScreen() {
    val cameras by SecurityRepository.cameras.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "Monitoreo de Cámaras", color = MinimalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(text = "Transmisión en directo y estado de dispositivos", color = MinimalTextSecondary, fontSize = 12.sp)
        }

        items(cameras) { camera ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MinimalSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MinimalSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Videocam, contentDescription = null, tint = MinimalPrimaryBlue)
                            Text(text = camera.name, color = MinimalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Surface(
                            color = if (camera.status.name == "OFFLINE") MinimalAccentRed.copy(alpha = 0.2f) else MinimalAccentGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = camera.status.name,
                                color = if (camera.status.name == "OFFLINE") MinimalAccentRed else MinimalAccentGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Text(text = "Ubicación: ${camera.locationZone}", color = MinimalTextSecondary, fontSize = 12.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Señal: ${camera.signalStrength}", color = MinimalTextSecondary, fontSize = 11.sp)
                        Text(text = "Almacenamiento: ${camera.storageType}", color = MinimalTextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}