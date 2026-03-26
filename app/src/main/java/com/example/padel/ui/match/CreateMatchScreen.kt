package com.example.padel.ui.match

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.padel.model.MatchCreate
import com.example.padel.network.RetrofitClient
import com.example.padel.session.SessionManager
import kotlinx.coroutines.launch

@Composable
fun CreateMatchScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var is1v1 by remember { mutableStateOf(true) }
    var isPrivate by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Crear partido",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("1 vs 1")
            Switch(
                checked = is1v1,
                onCheckedChange = { is1v1 = it }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Privado")
            Switch(
                checked = isPrivate,
                onCheckedChange = { isPrivate = it }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    message = ""

                    try {
                        val token = SessionManager.token

                        if (token.isNullOrBlank()) {
                            message = "Error: no token found. Please login again."
                        } else {
                            val match = MatchCreate(
                                is_1v1 = is1v1,
                                is_private = isPrivate,
                                host_id = null,
                                programmed_date = "2026-03-30"
                            )

                            RetrofitClient.api.createMatch(
                                authorization = "Bearer $token",
                                match = match
                            )

                            message = "Match creado ✅"
                            onBack()
                        }
                    } catch (e: Exception) {
                        message = "Error: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Creando..." else "Crear partido")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (message.isNotEmpty()) {
            Text(message)
        }
    }
}