package com.example.padel.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.example.padel.model.PlayerProfile
import com.example.padel.model.PlayerUpdateRequest
import com.example.padel.network.RetrofitClient
import com.example.padel.session.SessionManager
import kotlinx.coroutines.launch
import retrofit2.HttpException

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    var profile by remember { mutableStateOf<PlayerProfile?>(SessionManager.currentPlayer) }
    var loading by remember { mutableStateOf(profile == null) }
    var error by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var nivel by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (profile != null) return@LaunchedEffect
        val token = SessionManager.token
        if (token.isNullOrBlank()) {
            error = "Necesitas iniciar sesion primero."
            loading = false
            return@LaunchedEffect
        }
        try {
            profile = RetrofitClient.api.validateToken("Bearer $token").player
            SessionManager.currentPlayer = profile
            nombre = profile?.nombre.orEmpty()
            ciudad = profile?.ciudad.orEmpty()
            nivel = profile?.nivel.orEmpty()
        } catch (e: Exception) {
            error = e.message
        } finally {
            loading = false
        }
    }

    when {
        loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        error != null -> Box(modifier.fillMaxSize().padding(24.dp)) { Text("Error: ${error ?: "unknown"}") }
        profile != null -> {
            if (nombre.isBlank() && ciudad.isBlank() && nivel.isBlank()) {
                nombre = profile!!.nombre
                ciudad = profile!!.ciudad.orEmpty()
                nivel = profile!!.nivel.orEmpty()
            }

            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF58D39D), Color(0xFF53A7F6))
                                )
                            )
                            .padding(horizontal = 24.dp, vertical = 28.dp)
                    ) {
                        Text(
                            "Profile",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Your padel identity, level, and match activity in one place.",
                            color = Color.White.copy(alpha = 0.92f),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }

                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Card(
                            modifier = Modifier.offset(y = (-32).dp),
                            shape = RoundedCornerShape(30.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        ) {
                            Column(modifier = Modifier.padding(22.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top,
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(26.dp))
                                                .background(
                                                    Brush.horizontalGradient(
                                                        listOf(Color(0xFF58D39D), Color(0xFF53A7F6))
                                                    )
                                                )
                                                .padding(horizontal = 20.dp, vertical = 18.dp)
                                        ) {
                                            Text(
                                                text = profile!!.nombre.take(2).uppercase(),
                                                color = Color.White,
                                                style = MaterialTheme.typography.headlineMedium,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = profile!!.nombre,
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            InfoLine(Icons.Outlined.MailOutline, profile!!.email)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            InfoLine(Icons.Outlined.LocationOn, ciudad.ifBlank { "Madrid" })
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .padding(10.dp)
                                    ) {
                                        Icon(Icons.Outlined.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    HighlightChip(nivel.ifBlank { "amateur" }.replaceFirstChar { it.uppercase() })
                                    HighlightChip("Member")
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    MiniStat("4.7", "Rating")
                                    MiniStat("23", "Matches")
                                    MiniStat("65%", "Win rate")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Card(
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    if (isEditing) "Edit your profile" else "Profile details",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Keep your city and level updated so your matches feel more relevant.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(modifier = Modifier.height(18.dp))

                                OutlinedTextField(
                                    value = nombre,
                                    onValueChange = { nombre = it },
                                    enabled = isEditing,
                                    label = { Text("Name") },
                                    shape = RoundedCornerShape(18.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = ciudad,
                                    onValueChange = { ciudad = it },
                                    enabled = isEditing,
                                    label = { Text("City") },
                                    shape = RoundedCornerShape(18.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = nivel,
                                    onValueChange = { nivel = it },
                                    enabled = isEditing,
                                    label = { Text("Level") },
                                    placeholder = { Text("principiante / amateur / bueno / muy bueno") },
                                    shape = RoundedCornerShape(18.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                if (isEditing) {
                                    Button(
                                        onClick = {
                                            val token = SessionManager.token ?: return@Button
                                            scope.launch {
                                                try {
                                                    val updated = RetrofitClient.api.updatePlayer(
                                                        playerId = profile!!.id,
                                                        authorization = "Bearer $token",
                                                        player = PlayerUpdateRequest(
                                                            nombre = nombre,
                                                            ciudad = ciudad.ifBlank { null },
                                                            nivel = nivel.ifBlank { null },
                                                        )
                                                    )
                                                    profile = updated
                                                    SessionManager.currentPlayer = updated
                                                    isEditing = false
                                                    message = "Profile updated."
                                                    error = null
                                                } catch (e: HttpException) {
                                                    error = "HTTP ${e.code()} al actualizar el perfil."
                                                } catch (e: Exception) {
                                                    error = e.message
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(18.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(54.dp),
                                    ) {
                                        Text("Save changes")
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            isEditing = false
                                            nombre = profile!!.nombre
                                            ciudad = profile!!.ciudad.orEmpty()
                                            nivel = profile!!.nivel.orEmpty()
                                            message = null
                                        },
                                        shape = RoundedCornerShape(18.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = MaterialTheme.colorScheme.onSurface,
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp),
                                    ) {
                                        Text("Cancel")
                                    }
                                } else {
                                    Button(
                                        onClick = { isEditing = true },
                                        shape = RoundedCornerShape(18.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(54.dp),
                                    ) {
                                        Text("Edit profile")
                                    }
                                }

                                message?.let {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(it, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            ActivityCard(Icons.Outlined.TrendingUp, "65%", "Win rate", Modifier.weight(1f))
                            ActivityCard(Icons.Outlined.CalendarMonth, "23", "Matches", Modifier.weight(1f))
                            ActivityCard(Icons.Outlined.StarOutline, "4.7", "Rating", Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoLine(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = text,
            modifier = Modifier.padding(start = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun HighlightChip(text: String) {
    AssistChip(
        onClick = {},
        label = { Text(text) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.primary,
        )
    )
}

@Composable
private fun MiniStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ActivityCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 18.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
    }
}
