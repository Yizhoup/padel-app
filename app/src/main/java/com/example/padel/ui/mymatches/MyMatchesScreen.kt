package com.example.padel.ui.mymatches

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.padel.model.Match
import com.example.padel.network.RetrofitClient
import com.example.padel.session.SessionManager

@Composable
fun MyMatchesScreen(
    modifier: Modifier = Modifier,
    onJoinMatch: (String) -> Unit,
    onOpenMatchDetail: (String) -> Unit,
) {
    var matches by remember { mutableStateOf<List<Match>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var search by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val token = SessionManager.token
            if (token.isNullOrEmpty()) {
                error = "No token. Please login."
            } else {
                matches = RetrofitClient.api.getMatches("Bearer $token")
            }
        } catch (e: Exception) {
            error = e.message
        } finally {
            loading = false
        }
    }

    when {
        loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        error != null -> Box(modifier.fillMaxSize().padding(24.dp)) { Text("Error: $error") }
        else -> {
            val filtered = matches.filter {
                it.ubicacion.contains(search, ignoreCase = true) || it.estado.contains(search, ignoreCase = true)
            }
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 24.dp),
            ) {
                item {
                    Text(
                        "Find Matches",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(top = 24.dp, bottom = 18.dp),
                    )
                    OutlinedTextField(
                        value = search,
                        onValueChange = { search = it },
                        leadingIcon = { androidx.compose.material3.Icon(Icons.Outlined.Search, contentDescription = null) },
                        placeholder = { Text("Search by location or title...") },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = 18.dp, bottom = 22.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        FilterPill("All", selected = true)
                        FilterPill("Today")
                        FilterPill("Tomorrow")
                        FilterPill("Weekend")
                    }
                }

                if (filtered.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("No matches found.")
                        }
                    }
                } else {
                    items(filtered) { match ->
                        MatchCard(
                            match = match,
                            onJoinClick = onJoinMatch,
                            onOpenDetail = onOpenMatchDetail,
                            modifier = Modifier.padding(bottom = 18.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterPill(label: String, selected: Boolean = false) {
    val bg = if (selected) Color(0xFF63D59A) else Color(0xFFEAEDEF)
    val fg = if (selected) Color.White else Color(0xFF687385)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Text(
            text = label,
            color = fg,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
        )
    }
}
