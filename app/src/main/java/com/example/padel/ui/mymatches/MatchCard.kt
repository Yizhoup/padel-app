package com.example.padel.ui.mymatches

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.padel.model.Match
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun MatchCard(
    match: Match,
    modifier: Modifier = Modifier,
    onJoinClick: ((String) -> Unit)? = null,
    onOpenDetail: ((String) -> Unit)? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = match.ubicacion,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = onOpenDetail != null) {
                            onOpenDetail?.invoke(match.id.toString())
                        },
                )
                Text(
                    text = slotSummary(match),
                    color = Color(0xFF57D28C),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFEAF8F0))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            MatchMetaRow(Icons.Outlined.LocationOn, match.ubicacion)
            MatchMetaRow(Icons.Outlined.CalendarToday, formatMatchDateLabel(match.fecha))
            MatchMetaRow(Icons.Outlined.AccessTime, match.hora.removeSuffix(":00"))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MatchMetaRow(
                    icon = Icons.Outlined.People,
                    text = formatStatus(match.estado),
                    compact = true,
                )
                Text(
                    text = creatorLabel(match),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    if (match.estado.lowercase() == "abierto" && onJoinClick != null) {
                        onJoinClick(match.id.toString())
                    } else {
                        onOpenDetail?.invoke(match.id.toString())
                    }
                },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFF63D59A), Color(0xFF4AA3F0))
                        ),
                        shape = RoundedCornerShape(24.dp),
                    ),
            ) {
                Text(
                    text = if (match.estado.lowercase() == "abierto") "Join Match" else "View Details",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}

@Composable
private fun MatchMetaRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    compact: Boolean = false,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = if (compact) 0.dp else 6.dp),
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

private fun slotSummary(match: Match): String {
    val current = if (match.estado.lowercase() == "completo") match.plazas_totales else (match.plazas_totales / 2).coerceAtLeast(1)
    return "$current/${match.plazas_totales}"
}

private fun creatorLabel(match: Match): String = when (match.estado.lowercase()) {
    "abierto" -> "Open"
    "completo" -> "Full"
    else -> "Closed"
}

private fun formatStatus(status: String): String = when (status.lowercase()) {
    "abierto" -> "Intermediate"
    "completo" -> "Advanced"
    "cancelado" -> "Cancelled"
    else -> status
}

internal fun formatMatchDateLabel(isoDate: String): String {
    return try {
        val inFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outFmt = SimpleDateFormat("EEE, MMM d", Locale.US)
        outFmt.format(inFmt.parse(isoDate)!!)
    } catch (_: Exception) {
        isoDate
    }
}
