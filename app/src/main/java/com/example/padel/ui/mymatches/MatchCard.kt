package com.example.padel.ui.mymatches

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.padel.R
import com.example.padel.data.model.Court
import com.example.padel.data.model.Match
import com.example.padel.data.model.MatchStatus
import com.example.padel.data.model.PadelLevel
import com.example.padel.data.model.Player
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun MatchCard(
    match: Match,
    court: Court?,
    modifier: Modifier = Modifier,
    onJoinClick: ((String) -> Unit)? = null,
    onOpenDetail: ((String) -> Unit)? = null,
) {
    val spotsLeft = match.maxPlayers - match.currentPlayers.size
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = court?.name ?: stringResource(R.string.match_unknown_court),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (onOpenDetail != null) {
                                    Modifier.clickable { onOpenDetail(match.id) }
                                } else {
                                    Modifier
                                },
                            ),
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp),
                    ) {
                        Icon(
                            Icons.Default.Place,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = court?.address ?: "—",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 4.dp),
                        )
                    }
                }
                LevelBadge(level = match.level)
            }

            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = formatMatchDateLabel(match.date),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 6.dp),
            ) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(
                        R.string.match_time_duration,
                        match.time,
                        match.durationMinutes,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 6.dp),
            ) {
                Icon(
                    Icons.Default.Groups,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(
                        R.string.match_players_count,
                        match.currentPlayers.size,
                        match.maxPlayers,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp),
                )
                if (spotsLeft > 0 && match.status == MatchStatus.OPEN) {
                    Text(
                        text = pluralStringResource(R.plurals.match_spots_left, spotsLeft, spotsLeft),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }

            match.description?.takeIf { it.isNotBlank() }?.let { desc ->
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                PlayerAvatarRow(players = match.currentPlayers)
                when (match.status) {
                    MatchStatus.OPEN -> {
                        if (onJoinClick != null) {
                            Button(onClick = { onJoinClick(match.id) }) {
                                Text(stringResource(R.string.match_join))
                            }
                        }
                    }
                    MatchStatus.FULL -> {
                        AssistChip(
                            onClick = {},
                            label = { Text(stringResource(R.string.match_full)) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            ),
                        )
                    }
                    MatchStatus.COMPLETED -> {
                        AssistChip(
                            onClick = {},
                            label = { Text(stringResource(R.string.match_completed_badge)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelBadge(level: PadelLevel) {
    val (bg, fg) = levelBadgeColors(level)
    Text(
        text = stringResource(levelLabelRes(level)),
        style = MaterialTheme.typography.labelMedium,
        color = fg,
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    )
}

private fun levelBadgeColors(level: PadelLevel): Pair<Color, Color> {
    return when (level) {
        PadelLevel.PRINCIPIANTE -> Color(0xFFE8F5E9) to Color(0xFF1B5E20)
        PadelLevel.INTERMEDIO -> Color(0xFFE3F2FD) to Color(0xFF0D47A1)
        PadelLevel.AVANZADO -> Color(0xFFF3E5F5) to Color(0xFF4A148C)
        PadelLevel.PROFESIONAL -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        PadelLevel.TODOS_LOS_NIVELES -> Color(0xFFF5F5F5) to Color(0xFF424242)
    }
}

private fun levelLabelRes(level: PadelLevel): Int = when (level) {
    PadelLevel.PRINCIPIANTE -> R.string.level_principiante
    PadelLevel.INTERMEDIO -> R.string.level_intermedio
    PadelLevel.AVANZADO -> R.string.level_avanzado
    PadelLevel.PROFESIONAL -> R.string.level_profesional
    PadelLevel.TODOS_LOS_NIVELES -> R.string.level_todos
}

@Composable
private fun PlayerAvatarRow(players: List<Player>) {
        Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        players.take(3).forEach { player ->
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = player.name.take(1).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        if (players.size > 3) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "+${players.size - 3}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

internal fun formatMatchDateLabel(isoDate: String): String {
    return try {
        val inFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outFmt = SimpleDateFormat("EEE d MMM", Locale("es", "ES"))
        outFmt.format(inFmt.parse(isoDate)!!)
    } catch (_: Exception) {
        isoDate
    }
}
