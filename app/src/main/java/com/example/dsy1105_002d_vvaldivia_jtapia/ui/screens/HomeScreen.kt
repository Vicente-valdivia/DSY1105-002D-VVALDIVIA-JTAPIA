package com.example.dsy1105_002d_vvaldivia_jtapia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dsy1105_002d_vvaldivia_jtapia.data.repository.SecurityRepository
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.theme.*

@Composable
fun HomeScreen() {
    val user = SecurityRepository.currentUser
    val cameras by SecurityRepository.cameras.collectAsState()
    val events by SecurityRepository.events.collectAsState()

    val activeCameras = cameras.count { it.status.name != "OFFLINE" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Cabecera de Usuario ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MinimalPrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = user.initials, color = MinimalTextPrimary, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(text = "Hola, ${user.fullName}", color = MinimalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = user.assignedZone, color = MinimalTextSecondary, fontSize = 12.sp)
                    }
                }
                Surface(
                    color = MinimalSurface,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MinimalSurfaceBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MinimalAccentGreen))
                        Text(text = "EN LÍNEA", color = MinimalTextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- Tarjeta de Estado General ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MinimalSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MinimalSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Estado General", color = MinimalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(text = "INSERCODE • GUARDIÁN OS V2.4", color = MinimalTextSecondary, fontSize = 10.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Total Cámaras
                        Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MinimalBackground)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "TOTAL", color = MinimalTextSecondary, fontSize = 10.sp)
                                Text(text = "${cameras.size}", color = MinimalTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Cámaras", color = MinimalTextSecondary, fontSize = 11.sp)
                            }
                        }
                        // Activas
                        Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MinimalBackground)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "ACTIVAS", color = MinimalAccentGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(text = "$activeCameras", color = MinimalTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(text = "En línea", color = MinimalTextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                    LinearProgressIndicator(
                        progress = { 0.92f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = MinimalPrimaryBlue,
                        trackColor = MinimalBackground
                    )
                    Text(text = "Capacidad Operativa: 92%", color = MinimalTextSecondary, fontSize = 11.sp)
                }
            }
        }

        // --- Título Sección Cámaras ---
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Cámaras Asignadas", color = MinimalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                TextButton(onClick = { }) {
                    Text(text = "Ver todas", color = MinimalPrimaryBlue, fontSize = 12.sp)
                }
            }
        }

        // --- Listado rápido de Cámaras ---
        items(cameras.take(2)) { camera ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MinimalSurface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MinimalSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Surface(
                            color = if (camera.status.name == "OFFLINE") MinimalAccentRed.copy(alpha = 0.2f) else MinimalAccentGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (camera.status.name == "OFFLINE") "OFFLINE" else "REC",
                                color = if (camera.status.name == "OFFLINE") MinimalAccentRed else MinimalAccentGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(text = camera.timestamp, color = MinimalTextSecondary, fontSize = 11.sp)
                    }
                    Text(text = camera.name, color = MinimalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = camera.locationZone, color = MinimalTextSecondary, fontSize = 11.sp)
                }
            }
        }

        // --- Eventos en Vivo ---
        item {
            Text(text = "Eventos en vivo", color = MinimalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(top = 8.dp))
        }

        items(events) { event ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MinimalSurface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MinimalSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = when (event.severity.name) {
                            "CRITICAL" -> MinimalAccentRed
                            "WARNING" -> MinimalAccentAmber
                            else -> MinimalPrimaryBlue
                        }
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = event.title, color = MinimalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = event.timestamp, color = MinimalTextSecondary, fontSize = 11.sp)
                        }
                        Text(text = event.location, color = MinimalTextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        // Botón de Reportar Incidencia
        item {
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = MinimalPrimaryBlue),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = MinimalTextPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Reportar Incidencia", color = MinimalTextPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}