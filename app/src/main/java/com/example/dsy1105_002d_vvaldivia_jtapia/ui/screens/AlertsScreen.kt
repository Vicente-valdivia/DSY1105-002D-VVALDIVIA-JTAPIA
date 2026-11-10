package com.example.dsy1105_002d_vvaldivia_jtapia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dsy1105_002d_vvaldivia_jtapia.data.repository.SecurityRepository
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.theme.*

@Composable
fun AlertsScreen() {
    val events by SecurityRepository.events.collectAsState()
    val criticalAlerts = events.filter { it.severity.name == "CRITICAL" || it.severity.name == "WARNING" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "Panel de Alertas", color = MinimalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(text = "Incidentes que requieren atención inmediata", color = MinimalTextSecondary, fontSize = 12.sp)
        }

        items(criticalAlerts) { alert ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MinimalSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MinimalAccentRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Surface(color = MinimalAccentRed.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                            Text(text = alert.severity.name, color = MinimalAccentRed, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                        Text(text = alert.timestamp, color = MinimalTextSecondary, fontSize = 11.sp)
                    }
                    Text(text = alert.title, color = MinimalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = alert.location, color = MinimalTextSecondary, fontSize = 12.sp)
                    if (alert.aiConfidence != null) {
                        Text(text = "Confianza: ${alert.aiConfidence}", color = MinimalTextPrimary, fontSize = 11.sp)
                    }
                    Button(
                        onClick = { SecurityRepository.resolveEvent(alert.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = if (alert.isConfirmed) MinimalAccentGreen else MinimalPrimaryBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = if (alert.isConfirmed) "Alerta Confirmada / Atendida" else "Confirmar Alerta - Enviar a Central", color = MinimalTextPrimary)
                    }
                }
            }
        }
    }
}