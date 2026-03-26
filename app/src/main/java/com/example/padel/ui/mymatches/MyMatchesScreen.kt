package com.example.padel.ui.mymatches

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
        loading -> {
            Box(modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }

        error != null -> {
            Box(modifier.fillMaxSize()) {
                Text("Error: $error")
            }
        }

        else -> {
            LazyColumn(
                modifier = modifier.padding(16.dp)
            ) {
                items(matches) { match ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Match ID: ${match.id}")

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    onOpenMatchDetail(match.id.toString())
                                }
                            ) {
                                Text("View Detail")
                            }
                        }
                    }
                }
            }
        }
    }
}