package com.example.padel.ui.match

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.SportsTennis
import androidx.compose.material.icons.outlined.StickyNote2
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.example.padel.model.MatchCreate
import com.example.padel.network.RetrofitClient
import com.example.padel.session.SessionManager
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateMatchScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val levelOptions = listOf("iniciacion", "intermedio", "avanzado", "cualquiera")
    val slotOptions = listOf(2, 4)
    val districtOptions = listOf("Centro", "Salamanca", "Chamartin", "Retiro", "Moncloa")
    val courtsByDistrict = mapOf(
        "Centro" to listOf("La Latina Club", "Padel Sol", "Plaza Central Courts"),
        "Salamanca" to listOf("Goya Padel Hub", "Lista Indoor", "Velazquez Courts"),
        "Chamartin" to listOf("Bernabeu Padel", "Chamartin Arena", "Castilla Racquet Club"),
        "Retiro" to listOf("Parque Central Padel", "Retiro Club", "Ibiza Match Point"),
        "Moncloa" to listOf("Green Park Courts", "Moncloa Match Point", "Ciudad Universitaria Padel"),
    )

    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var district by remember { mutableStateOf(districtOptions.first()) }
    var selectedCourt by remember { mutableStateOf(courtsByDistrict[district].orEmpty().first()) }
    var nivelRequerido by remember { mutableStateOf("intermedio") }
    var plazasTotales by remember { mutableStateOf(4) }
    var descripcion by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var districtExpanded by remember { mutableStateOf(false) }
    var courtExpanded by remember { mutableStateOf(false) }

    val headerBrush = Brush.horizontalGradient(
        listOf(Color(0xFF58D39D), Color(0xFF53A7F6))
    )

    val openDatePicker = {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                fecha = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000L
        }.show()
    }

    val openTimePicker = {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                hora = String.format("%02d:%02d:00", hourOfDay, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true,
        ).show()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerBrush)
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Text(
                    text = "Create Match",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Organize your next padel game with a cleaner setup.",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.92f),
                )
            }
        }

        item {
            Column(modifier = Modifier.padding(20.dp)) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        SectionTitle(
                            icon = Icons.Outlined.LocationOn,
                            title = "Choose location",
                            subtitle = "Pick a district first, then choose one of the available clubs."
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ExposedDropdownMenuBox(
                            expanded = districtExpanded,
                            onExpandedChange = { districtExpanded = !districtExpanded },
                        ) {
                            OutlinedTextField(
                                value = district,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("District") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtExpanded) },
                                shape = RoundedCornerShape(18.dp),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                            )
                            ExposedDropdownMenu(
                                expanded = districtExpanded,
                                onDismissRequest = { districtExpanded = false },
                            ) {
                                districtOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            district = option
                                            selectedCourt = courtsByDistrict[option].orEmpty().first()
                                            districtExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        ExposedDropdownMenuBox(
                            expanded = courtExpanded,
                            onExpandedChange = { courtExpanded = !courtExpanded },
                        ) {
                            OutlinedTextField(
                                value = selectedCourt,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Club / Court") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = courtExpanded) },
                                shape = RoundedCornerShape(18.dp),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                            )
                            ExposedDropdownMenu(
                                expanded = courtExpanded,
                                onDismissRequest = { courtExpanded = false },
                            ) {
                                courtsByDistrict[district].orEmpty().forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            selectedCourt = option
                                            courtExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.7f))
                                        .padding(10.dp)
                                ) {
                                    Icon(Icons.Outlined.Apartment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                                Column {
                                    Text("Selected venue", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        "$selectedCourt, $district",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        SectionTitle(
                            icon = Icons.Outlined.CalendarMonth,
                            title = "Schedule",
                            subtitle = "Choose a future date and time for the match."
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            PickerCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Outlined.CalendarMonth,
                                label = "Date",
                                value = fecha.ifBlank { "Select date" },
                                onClick = openDatePicker,
                            )
                            PickerCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Outlined.Timer,
                                label = "Time",
                                value = hora.ifBlank { "Select time" },
                                onClick = openTimePicker,
                            )
                        }

                        if (fecha.isNotBlank() || hora.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = buildSchedulePreview(fecha = fecha, hora = hora),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        SectionTitle(
                            icon = Icons.Outlined.SportsTennis,
                            title = "Players and level",
                            subtitle = "Set who this game is for."
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Skill level", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(10.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            levelOptions.forEach { option ->
                                val selected = nivelRequerido == option
                                AssistChip(
                                    onClick = { nivelRequerido = option },
                                    label = {
                                        Text(option.replaceFirstChar { it.uppercase() })
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        labelColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text("Max players", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(10.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            slotOptions.forEach { option ->
                                val selected = plazasTotales == option
                                AssistChip(
                                    onClick = { plazasTotales = option },
                                    label = { Text("$option players") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.Groups,
                                            contentDescription = null,
                                            tint = if (selected) Color.White else MaterialTheme.colorScheme.primary,
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        labelColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        SectionTitle(
                            icon = Icons.Outlined.StickyNote2,
                            title = "Notes",
                            subtitle = "Add any extra details players should know before joining."
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            label = { Text("Description") },
                            placeholder = { Text("Level of play, meeting point, or anything useful") },
                            shape = RoundedCornerShape(18.dp),
                            minLines = 4,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            message = ""
                            try {
                                val token = SessionManager.token
                                if (token.isNullOrBlank()) {
                                    message = "Error: no token found. Please login again."
                                } else if (fecha.isBlank() || hora.isBlank()) {
                                    message = "Selecciona fecha y hora."
                                } else {
                                    val selectedDate = LocalDate.parse(fecha)
                                    val selectedTime = LocalTime.parse(hora)
                                    val nowDate = LocalDate.now()
                                    val nowTime = LocalTime.now()

                                    if (selectedDate.isBefore(nowDate) ||
                                        (selectedDate.isEqual(nowDate) && selectedTime.isBefore(nowTime))
                                    ) {
                                        message = "No puedes crear un partido en una fecha u hora pasada."
                                        isLoading = false
                                        return@launch
                                    }

                                    val player = SessionManager.currentPlayer
                                        ?: RetrofitClient.api.validateToken("Bearer $token").player.also {
                                            SessionManager.currentPlayer = it
                                        }

                                    val match = MatchCreate(
                                        fecha = fecha,
                                        hora = hora,
                                        ubicacion = "$selectedCourt, $district",
                                        nivel_requerido = nivelRequerido,
                                        plazas_totales = plazasTotales,
                                        descripcion = descripcion.ifBlank { null },
                                        creador_id = player.id,
                                    )

                                    RetrofitClient.api.createMatch(
                                        authorization = "Bearer $token",
                                        match = match,
                                    )

                                    message = "Partido creado"
                                    onBack()
                                }
                            } catch (e: HttpException) {
                                message = "Error HTTP ${e.code()}: revisa los datos del formulario."
                            } catch (e: Exception) {
                                message = "Error: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading,
                ) {
                    Text(if (isLoading) "Creating..." else "Create match", style = MaterialTheme.typography.titleMedium)
                }

                if (message.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = message,
                        color = if (message.startsWith("Error")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SectionTitle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(10.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Column {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PickerCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(12.dp))
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun buildSchedulePreview(fecha: String, hora: String): String {
    return runCatching {
        val date = if (fecha.isNotBlank()) {
            LocalDate.parse(fecha).format(DateTimeFormatter.ofPattern("EEE, MMM d"))
        } else {
            "date pending"
        }
        val time = if (hora.isNotBlank()) hora.take(5) else "time pending"
        "Scheduled for $date at $time"
    }.getOrElse { "Schedule not ready yet" }
}
