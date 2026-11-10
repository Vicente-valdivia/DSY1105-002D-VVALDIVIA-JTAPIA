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
fun EventsScreen() {
    val events by SecurityRepository.events.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "Historial de Registros", color = MinimalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(text = "Turno actual • 27 Oct - 14:00 - 22:00", color = MinimalTextSecondary, fontSize = 12.sp)
        }

        items(events) { event ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MinimalSurface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MinimalSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = event.title, color = MinimalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = event.timestamp, color = MinimalTextSecondary, fontSize = 12.sp)
                    }
                    Text(text = event.location, color = MinimalTextSecondary, fontSize = 12.sp)
                    if (event.aiConfidence != null) {
                        Text(text = "IA: ${event.aiConfidence}", color = MinimalAccentAmber, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}