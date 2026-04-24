package com.example.padel.ui.match

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.padel.R
import com.example.padel.model.Match
import com.example.padel.model.MatchUpdateRequest
import com.example.padel.network.RetrofitClient
import com.example.padel.session.SessionManager
import com.example.padel.ui.mymatches.formatMatchDateLabel
import kotlinx.coroutines.launch
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    matchId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var match by remember { mutableStateOf<Match?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var joinMessage by remember { mutableStateOf<String?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var nivel by remember { mutableStateOf("") }
    var plazas by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(matchId) {
        val token = SessionManager.token
        val id = matchId.toIntOrNull()
        if (token.isNullOrBlank() || id == null) {
            error = "Partido no válido o sesión no iniciada."
            loading = false
            return@LaunchedEffect
        }

        try {
            match = RetrofitClient.api.getMatch(id, "Bearer $token")
            match?.let {
                fecha = it.fecha
                hora = it.hora
                ubicacion = it.ubicacion
                plazas = it.plazas_totales.toString()
            }
        } catch (e: Exception) {
            error = e.message
        } finally {
            loading = false
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.screen_match_detail)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { innerPadding ->
        when {
            loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                ) {
                    Text("Error: ${error ?: "unknown"}")
                }
            }

            match != null -> {
                val isCreator = SessionManager.currentPlayer?.id == match!!.creador_id
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF4F8D6A), Color(0xFF7C9F8A))
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column {
                            Text(
                                text = match!!.ubicacion,
                                color = Color.White,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Organiza y gestiona este partido de padel",
                                color = Color.White.copy(alpha = 0.92f),
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(24.dp)) {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                MatchDetailRow(Icons.Outlined.LocationOn, "Ubicación", match!!.ubicacion)
                                MatchDetailRow(Icons.Outlined.CalendarMonth, "Fecha", formatMatchDateLabel(match!!.fecha))
                                MatchDetailRow(Icons.Outlined.AccessTime, "Hora", match!!.hora.removeSuffix(":00"))
                                MatchDetailRow(Icons.Outlined.Groups, "Plazas", match!!.plazas_totales.toString())
                                MatchDetailRow(Icons.Outlined.PersonOutline, "Creador", (match!!.creador_id ?: "desconocido").toString())

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Estado",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = match!!.estado.replaceFirstChar { it.uppercase() },
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        }

                        if (isCreator) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Card(
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text("Gestionar partido", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(14.dp))

                                    if (isEditing) {
                                        OutlinedTextField(
                                            value = fecha,
                                            onValueChange = { fecha = it },
                                            label = { Text("Fecha") },
                                            shape = RoundedCornerShape(16.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = hora,
                                            onValueChange = { hora = it },
                                            label = { Text("Hora") },
                                            shape = RoundedCornerShape(16.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = ubicacion,
                                            onValueChange = { ubicacion = it },
                                            label = { Text("Ubicación") },
                                            shape = RoundedCornerShape(16.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = plazas,
                                            onValueChange = { plazas = it.filter(Char::isDigit) },
                                            label = { Text("Plazas totales") },
                                            shape = RoundedCornerShape(16.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Button(
                                            onClick = {
                                                val token = SessionManager.token ?: return@Button
                                                scope.launch {
                                                    try {
                                                        loading = true
                                                        match = RetrofitClient.api.updateMatch(
                                                            matchId = match!!.id,
                                                            authorization = "Bearer $token",
                                                            match = MatchUpdateRequest(
                                                                fecha = fecha,
                                                                hora = hora,
                                                                ubicacion = ubicacion,
                                                                nivel_requerido = nivel.ifBlank { null },
                                                                plazas_totales = plazas.toIntOrNull(),
                                                                descripcion = descripcion.ifBlank { null },
                                                            )
                                                        )
                                                        isEditing = false
                                                        joinMessage = "Partido actualizado."
                                                    } catch (e: HttpException) {
                                                        error = "HTTP ${e.code()} al actualizar el partido."
                                                    } catch (e: Exception) {
                                                        error = e.message
                                                    } finally {
                                                        loading = false
                                                    }
                                                }
                                            },
                                            shape = RoundedCornerShape(18.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Guardar partido")
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = { isEditing = false },
                                            shape = RoundedCornerShape(18.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                contentColor = MaterialTheme.colorScheme.onSurface,
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Cancelar edición")
                                        }
                                    } else {
                                        Button(
                                            onClick = { isEditing = true },
                                            shape = RoundedCornerShape(18.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Editar partido")
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = {
                                                val token = SessionManager.token ?: return@Button
                                                scope.launch {
                                                    try {
                                                        RetrofitClient.api.deleteMatch(match!!.id, "Bearer $token")
                                                        onBack()
                                                    } catch (e: HttpException) {
                                                        error = "HTTP ${e.code()} al borrar el partido."
                                                    } catch (e: Exception) {
                                                        error = e.message
                                                    }
                                                }
                                            },
                                            shape = RoundedCornerShape(18.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                contentColor = MaterialTheme.colorScheme.onSurface,
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Eliminar partido")
                                        }
                                    }
                                }
                            }
                        }

                        if (match!!.estado.lowercase() == "abierto") {
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    val token = SessionManager.token ?: return@Button
                                    val id = match!!.id
                                    scope.launch {
                                        loading = true
                                        error = null
                                        joinMessage = null
                                        try {
                                            val response = RetrofitClient.api.joinMatch(id, "Bearer $token")
                                            joinMessage = "${response.message}. Plazas restantes: ${response.plazas_restantes}"
                                            match = RetrofitClient.api.getMatch(id, "Bearer $token")
                                        } catch (e: Exception) {
                                            error = e.message
                                        } finally {
                                            loading = false
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White,
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Unirse al partido")
                            }
                        }

                        joinMessage?.let {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(it, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Column {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge)
        }
    }
}
