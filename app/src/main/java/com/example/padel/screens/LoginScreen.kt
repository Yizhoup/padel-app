package com.example.padel.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.padel.network.RetrofitClient
import com.example.padel.session.SessionManager
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onOpenRegister: () -> Unit,
) {
    var username by remember { mutableStateOf(SessionManager.prefilledEmail ?: "") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val gradient = Brush.horizontalGradient(listOf(Color(0xFF63D59A), Color(0xFF4AA3F0)))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFEFF8F3), MaterialTheme.colorScheme.background)
                        )
                    )
                    .padding(top = 78.dp, start = 24.dp, end = 24.dp, bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(gradient),
                        contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(58.dp),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "PadelMatch",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Find your perfect match",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(22.dp),
                ) {
                    Text("Welcome back", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(28.dp))

                    Text("Email", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        leadingIcon = {
                            Icon(Icons.Outlined.Email, contentDescription = null)
                        },
                        placeholder = { Text("your@email.com") },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    Text("Password", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        leadingIcon = {
                            Icon(Icons.Outlined.Lock, contentDescription = null)
                        },
                        placeholder = { Text("••••••••") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        "Forgot password?",
                        color = Color(0xFF63D59A),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.End),
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    val result = RetrofitClient.api.login(username, password)
                                    SessionManager.token = result.access_token
                                    SessionManager.currentPlayer =
                                        RetrofitClient.api.validateToken("Bearer ${result.access_token}").player
                                    SessionManager.prefilledEmail = username
                                    message = "Acceso correcto."
                                    onLoginSuccess()
                                } catch (e: Exception) {
                                    message = "Error: ${e.message}"
                                }
                            }
                        },
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(gradient, RoundedCornerShape(22.dp))
                                .padding(vertical = 18.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Sign In", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                            Icon(Icons.Outlined.ArrowForward, contentDescription = null, tint = Color.White)
                        }
                    }

                    if (message.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            message,
                            color = if (message.startsWith("Error")) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "Don't have an account? ",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            "Sign Up",
                            color = Color(0xFF63D59A),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onOpenRegister() },
                        )
                    }
                }
            }
        }
    }
}
