package com.example.dsy1105_002d_vvaldivia_jtapia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.Camera
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.CameraStatus
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.EventSeverity
import com.example.dsy1105_002d_vvaldivia_jtapia.data.model.SecurityEvent
import com.example.dsy1105_002d_vvaldivia_jtapia.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Inicio. Cada bloque es interactivo; la navegación se recibe por parámetros (state hoisting)
 * y [onWatchCamera] queda como punto de enganche para la futura transmisión en vivo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenCameras: () -> Unit = {},
    onOpenEvents: () -> Unit = {},
    onOpenAlerts: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    /** Futuro: abrir el visor de video. Si es null se avisa que aún no está disponible. */
    onWatchCamera: ((Camera) -> Unit)? = null,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var selectedCameraId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedEventId by rememberSaveable { mutableStateOf<String?>(null) }
    var showIncidentDialog by rememberSaveable { mutableStateOf(false) }

    // Se busca el elemento por id en el estado vivo: el detalle siempre refleja el dato actual
    val selectedCamera = state.cameras.find { it.id == selectedCameraId }
    val selectedEvent = state.recentEvents.find { it.id == selectedEventId }

    fun showMessage(message: String) {
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MinimalBackground)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Cabecera de Usuario ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar + saludo -> Perfil
                    Row(
                        modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable(onClick = onOpenProfile),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(44.dp).clip(CircleShape).background(MinimalPrimaryBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = state.initials, color = MinimalTextPrimary, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text(text = "Hola, ${state.displayName}", color = MinimalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = state.zone, color = MinimalTextSecondary, fontSize = 12.sp)
                        }
                    }
                    // Chip de disponibilidad (tocar para cambiar)
                    val statusColor = if (state.isOnline) MinimalAccentGreen else MinimalTextSecondary
                    Surface(
                        onClick = {
                            viewModel.toggleOnline()
                            showMessage(if (state.isOnline) "Ahora estás fuera de línea" else "Ahora estás en línea")
                        },
                        color = MinimalSurface,
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, MinimalSurfaceBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(statusColor))
                            Text(
                                text = if (state.isOnline) "EN LÍNEA" else "FUERA DE LÍNEA",
                                color = MinimalTextPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
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
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatCard("TOTAL", "${state.cameras.size}", "Cámaras", MinimalTextSecondary, Modifier.weight(1f), onOpenCameras)
                            StatCard("ACTIVAS", "${state.activeCameras}", "En línea", MinimalAccentGreen, Modifier.weight(1f), onOpenCameras)
                            StatCard(
                                "ALERTAS",
                                "${state.pendingAlerts}",
                                "Pendientes",
                                if (state.pendingAlerts > 0) MinimalAccentRed else MinimalAccentGreen,
                                Modifier.weight(1f),
                                onOpenAlerts
                            )
                        }
                        LinearProgressIndicator(
                            progress = { state.operationalRatio },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = MinimalPrimaryBlue,
                            trackColor = MinimalBackground
                        )
                        Text(
                            text = "Capacidad Operativa: ${(state.operationalRatio * 100).toInt()}%",
                            color = MinimalTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // --- Cámaras ---
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Cámaras Asignadas", color = MinimalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    TextButton(onClick = onOpenCameras) {
                        Text(text = "Ver todas", color = MinimalPrimaryBlue, fontSize = 12.sp)
                    }
                }
            }

            items(state.cameras.take(2), key = { it.id }) { camera ->
                Card(
                    onClick = { selectedCameraId = camera.id },
                    colors = CardDefaults.cardColors(containerColor = MinimalSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MinimalSurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            StatusBadge(cameraStatusLabel(camera.status), cameraStatusColor(camera.status))
                            Text(text = camera.timestamp, color = MinimalTextSecondary, fontSize = 11.sp)
                        }
                        Text(text = camera.name, color = MinimalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = camera.locationZone, color = MinimalTextSecondary, fontSize = 11.sp)
                    }
                }
            }

            // --- Eventos en vivo ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Eventos en vivo", color = MinimalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    TextButton(onClick = onOpenEvents) {
                        Text(text = "Ver registros", color = MinimalPrimaryBlue, fontSize = 12.sp)
                    }
                }
            }

            if (state.recentEvents.isEmpty()) {
                item {
                    Text(text = "Sin eventos registrados en este turno.", color = MinimalTextSecondary, fontSize = 12.sp)
                }
            }

            items(state.recentEvents, key = { it.id }) { event ->
                Card(
                    onClick = { selectedEventId = event.id },
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
                            imageVector = if (event.isConfirmed) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = if (event.isConfirmed) "Atendido" else null,
                            tint = if (event.isConfirmed) MinimalAccentGreen else severityColor(event.severity)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    text = event.title,
                                    color = MinimalTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(text = event.timestamp, color = MinimalTextSecondary, fontSize = 11.sp)
                            }
                            Text(text = event.location, color = MinimalTextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }

            // --- Reportar incidencia ---
            item {
                Button(
                    onClick = { showIncidentDialog = true },
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

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        )
    }

    // --- Detalle de cámara ---
    if (selectedCamera != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedCameraId = null },
            containerColor = MinimalSurface
        ) {
            CameraDetail(
                camera = selectedCamera,
                onWatch = {
                    if (onWatchCamera != null) onWatchCamera(selectedCamera)
                    else showMessage("La transmisión en vivo estará disponible próximamente")
                    selectedCameraId = null
                },
                onOpenCameras = {
                    selectedCameraId = null
                    onOpenCameras()
                }
            )
        }
    }

    // --- Detalle de evento ---
    if (selectedEvent != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedEventId = null },
            containerColor = MinimalSurface
        ) {
            EventDetail(
                event = selectedEvent,
                onConfirm = {
                    viewModel.confirmEvent(selectedEvent.id)
                    showMessage("Evento ${selectedEvent.id} marcado como atendido")
                },
                onOpenAlerts = {
                    selectedEventId = null
                    onOpenAlerts()
                }
            )
        }
    }

    // --- Formulario de incidencia ---
    if (showIncidentDialog) {
        IncidentDialog(
            cameras = state.cameras,
            onDismiss = { showIncidentDialog = false },
            onSubmit = { title, location, severity, cameraCode ->
                if (viewModel.reportIncident(title, location, severity, cameraCode)) {
                    showIncidentDialog = false
                    showMessage("Incidencia reportada")
                }
            }
        )
    }
}

// ───────────────────────── Componentes ─────────────────────────

@Composable
private fun StatCard(
    label: String,
    value: String,
    caption: String,
    labelColor: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MinimalBackground)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, color = labelColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(text = value, color = MinimalTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = caption, color = MinimalTextSecondary, fontSize = 11.sp)
        }
    }
}

@Composable
private fun StatusBadge(text: String, color: Color) {
    Surface(color = color.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = MinimalTextSecondary, fontSize = 12.sp)
        Text(text = value, color = MinimalTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun CameraDetail(camera: Camera, onWatch: () -> Unit, onOpenCameras: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = camera.name, color = MinimalTextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            StatusBadge(cameraStatusLabel(camera.status), cameraStatusColor(camera.status))
        }
        DetailRow("Código", camera.id)
        DetailRow("Ubicación", camera.locationZone)
        DetailRow("Última señal", camera.timestamp)
        DetailRow("Señal", camera.signalStrength)
        DetailRow("Almacenamiento", camera.storageType)
        DetailRow("Calidad", if (camera.isHd) "HD" else "SD")
        Spacer(Modifier.height(6.dp))
        Button(
            onClick = onWatch,
            enabled = camera.status != CameraStatus.OFFLINE,
            colors = ButtonDefaults.buttonColors(containerColor = MinimalPrimaryBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (camera.status == CameraStatus.OFFLINE) "Cámara sin conexión" else "Ver transmisión",
                color = MinimalTextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        OutlinedButton(onClick = onOpenCameras, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Text("Ir a Cámaras", color = MinimalTextPrimary)
        }
    }
}

@Composable
private fun EventDetail(event: SecurityEvent, onConfirm: () -> Unit, onOpenAlerts: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = event.title,
                color = MinimalTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )
            StatusBadge(severityLabel(event.severity), severityColor(event.severity))
        }
        DetailRow("Código", event.id)
        DetailRow("Hora", event.timestamp)
        DetailRow("Ubicación", event.location)
        event.cameraCode?.let { DetailRow("Cámara", it) }
        event.durationSeconds?.let { DetailRow("Duración", "$it s") }
        event.aiConfidence?.let { DetailRow("Análisis IA", it) }
        DetailRow("Estado", if (event.isConfirmed) "Atendido" else "Pendiente")
        Spacer(Modifier.height(6.dp))
        Button(
            onClick = onConfirm,
            enabled = !event.isConfirmed,
            colors = ButtonDefaults.buttonColors(containerColor = MinimalPrimaryBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (event.isConfirmed) "Evento atendido" else "Marcar como atendido",
                color = MinimalTextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        OutlinedButton(onClick = onOpenAlerts, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Text("Ir a Alertas", color = MinimalTextPrimary)
        }
    }
}

@Composable
private fun IncidentDialog(
    cameras: List<Camera>,
    onDismiss: () -> Unit,
    onSubmit: (title: String, location: String, severity: EventSeverity, cameraCode: String?) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var severityName by rememberSaveable { mutableStateOf(EventSeverity.WARNING.name) }
    var cameraCode by rememberSaveable { mutableStateOf<String?>(null) }
    val severity = EventSeverity.valueOf(severityName)
    val titleValid = title.trim().length >= HomeViewModel.MIN_TITLE_LENGTH

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MinimalPrimaryBlue,
        unfocusedBorderColor = MinimalSurfaceBorder,
        focusedTextColor = MinimalTextPrimary,
        unfocusedTextColor = MinimalTextPrimary
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MinimalSurface,
        title = { Text("Reportar incidencia", color = MinimalTextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("¿Qué ocurrió?", color = MinimalTextSecondary) },
                    isError = title.isNotEmpty() && !titleValid,
                    supportingText = if (title.isNotEmpty() && !titleValid) {
                        { Text("Mínimo ${HomeViewModel.MIN_TITLE_LENGTH} caracteres", color = MinimalAccentRed) }
                    } else null,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Ubicación (opcional)", color = MinimalTextSecondary) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Gravedad", color = MinimalTextSecondary, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EventSeverity.entries.forEach { option ->
                        FilterChip(
                            selected = severity == option,
                            onClick = { severityName = option.name },
                            label = { Text(severityLabel(option), fontSize = 11.sp) }
                        )
                    }
                }

                if (cameras.isNotEmpty()) {
                    Text("Cámara relacionada (opcional)", color = MinimalTextSecondary, fontSize = 12.sp)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        cameras.forEach { cam ->
                            FilterChip(
                                selected = cameraCode == cam.id,
                                onClick = { cameraCode = if (cameraCode == cam.id) null else cam.id },
                                label = { Text("${cam.id} • ${cam.name}", fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = titleValid,
                onClick = {
                    val related = cameras.find { it.id == cameraCode }
                    val place = location.ifBlank { related?.let { "${it.name} - ${it.locationZone}" } ?: "" }
                    onSubmit(title, place, severity, cameraCode)
                }
            ) { Text("Enviar", color = if (titleValid) MinimalPrimaryBlue else MinimalTextSecondary) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = MinimalTextSecondary) }
        }
    )
}

// ───────────────────────── Helpers de presentación ─────────────────────────

private fun cameraStatusLabel(status: CameraStatus) = when (status) {
    CameraStatus.ONLINE -> "EN LÍNEA"
    CameraStatus.RECORDING -> "REC"
    CameraStatus.OFFLINE -> "OFFLINE"
}

private fun cameraStatusColor(status: CameraStatus) =
    if (status == CameraStatus.OFFLINE) MinimalAccentRed else MinimalAccentGreen

private fun severityLabel(severity: EventSeverity) = when (severity) {
    EventSeverity.INFO -> "Informativa"
    EventSeverity.WARNING -> "Advertencia"
    EventSeverity.CRITICAL -> "Crítica"
}

private fun severityColor(severity: EventSeverity) = when (severity) {
    EventSeverity.CRITICAL -> MinimalAccentRed
    EventSeverity.WARNING -> MinimalAccentAmber
    EventSeverity.INFO -> MinimalPrimaryBlue
}
