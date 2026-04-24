package com.example.padel.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.padel.model.PlayerCreateRequest
import com.example.padel.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun RegisterScreen(
    onRegisterSuccess: (String) -> Unit,
    onBackToLogin: () -> Unit,
) {
    val levelOptions = listOf("principiante", "amateur", "bueno", "muy bueno")
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var nivel by remember { mutableStateOf("amateur") }
    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Crear cuenta", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Únete para publicar partidos y gestionar tus reservas.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = ciudad,
                        onValueChange = { ciudad = it },
                        label = { Text("Ciudad") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = nivel,
                        onValueChange = { if (it in levelOptions || it.isBlank()) nivel = it },
                        label = { Text("Nivel") },
                        supportingText = { Text("Opciones: ${levelOptions.joinToString()}") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                message = ""
                                try {
                                    if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
                                        message = "Completa nombre, email y contraseña."
                                    } else if (!email.contains("@") || !email.contains(".")) {
                                        message = "Introduce un email válido."
                                    } else {
                                        RetrofitClient.api.register(
                                            PlayerCreateRequest(
                                                nombre = nombre,
                                                email = email,
                                                password = password,
                                                nivel = nivel.ifBlank { null },
                                                ciudad = ciudad.ifBlank { null },
                                            )
                                        )
                                        onRegisterSuccess(email)
                                    }
                                } catch (e: HttpException) {
                                    message = when (e.code()) {
                                        400 -> "Ese email ya existe o no se pudo crear la cuenta."
                                        422 -> "Revisa el formato del email y los campos obligatorios."
                                        else -> "Error HTTP ${e.code()} al crear la cuenta."
                                    }
                                } catch (e: Exception) {
                                    message = "Error: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Text(if (isLoading) "Creando..." else "Crear cuenta")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onBackToLogin,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Volver al login")
                    }

                    if (message.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(message, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}
