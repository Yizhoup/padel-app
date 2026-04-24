package com.example.padel.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.padel.model.Match
import com.example.padel.network.RetrofitClient
import com.example.padel.session.SessionManager
import com.example.padel.ui.mymatches.MatchCard
import retrofit2.HttpException

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onJoinMatch: (String) -> Unit,
    onOpenMatchDetail: (String) -> Unit,
) {
    var matches by remember { mutableStateOf<List<Match>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val token = SessionManager.token
        if (token.isNullOrBlank()) {
            error = "Necesitas iniciar sesión primero."
            loading = false
            return@LaunchedEffect
        }

        try {
            matches = RetrofitClient.api.getAvailableMatches("Bearer $token")
        } catch (e: HttpException) {
            if (e.code() == 404) matches = emptyList() else error = "HTTP ${e.code()} ${e.message()}"
        } catch (e: Exception) {
            error = e.message
        } finally {
            loading = false
        }
    }

    when {
        loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        error != null -> Box(modifier.fillMaxSize().padding(24.dp)) { Text("Error: ${error ?: "unknown"}") }
        else -> {
            val playerName = SessionManager.currentPlayer?.nombre?.trim().orEmpty().ifBlank { "Player" }
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            ) {
                item {
                    Column(modifier = Modifier.padding(bottom = 20.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF4D8B69), Color(0xFF7EA08B))
                                    )
                                )
                                .padding(24.dp)
                        ) {
                            Column {
                                Text(
                                    "Hey, $playerName!",
                                    color = Color.White,
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    "Ready to play?",
                                    color = Color.White.copy(alpha = 0.9f),
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(top = 8.dp),
                                )

                                Row(
                                    modifier = Modifier.padding(top = 28.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    StatCard("2,547", "Active Players", Icons.Outlined.People, Modifier.weight(1f))
                                    StatCard(matches.size.toString(), "Matches Today", Icons.Outlined.CalendarMonth, Modifier.weight(1f))
                                    StatCard(
                                        (SessionManager.currentPlayer?.id ?: 0).toString(),
                                        "Your Profile",
                                        Icons.Outlined.LocationOn,
                                        Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                        ) {
                            ActionTile("Create", "New Match", Color(0xFF5A9772), Color(0xFF88A791), Modifier.weight(1f))
                            ActionTile("Find", "Nearby", Color(0xFF738F7E), Color(0xFF95A89C), Modifier.weight(1f))
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Nearby Matches", style = MaterialTheme.typography.headlineMedium)
                            Text("View All", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }

                if (matches.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("No nearby matches yet.")
                        }
                    }
                } else {
                    items(matches.take(4)) { match ->
                        MatchCard(
                            match = match,
                            onJoinClick = onJoinMatch,
                            onOpenDetail = onOpenMatchDetail,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.14f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(icon, contentDescription = null, tint = Color.White)
            Text(value, color = Color.White, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(top = 12.dp))
            Text(label, color = Color.White.copy(alpha = 0.92f), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ActionTile(title: String, subtitle: String, start: Color, end: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(start, end)))
            .padding(24.dp)
    ) {
        Column {
            Text(title, color = Color.White, style = MaterialTheme.typography.headlineMedium)
            Text(subtitle, color = Color.White.copy(alpha = 0.92f), style = MaterialTheme.typography.titleLarge)
        }
    }
}
