package com.example.padel.data.model

enum class MatchStatus(val wireValue: String) {
    OPEN("open"),
    FULL("full"),
    COMPLETED("completed");

    companion object {
        fun fromWire(value: String): MatchStatus =
            entries.firstOrNull { it.wireValue == value } ?: OPEN
    }
}

enum class PadelLevel(val wireValue: String) {
    PRINCIPIANTE("Principiante"),
    INTERMEDIO("Intermedio"),
    AVANZADO("Avanzado"),
    PROFESIONAL("Profesional"),
    TODOS_LOS_NIVELES("Todos los Niveles");

    companion object {
        fun fromWire(value: String): PadelLevel =
            entries.firstOrNull { it.wireValue == value } ?: INTERMEDIO
    }
}

data class Player(
    val id: String,
    val name: String,
    val avatarUrl: String? = null,
    val level: PadelLevel,
    val rating: Double,
    val matchesPlayed: Int,
)

data class Court(
    val id: String,
    val name: String,
    val address: String,
    val imageUrl: String,
    val pricePerHour: Int,
    val indoor: Boolean,
    val amenities: List<String>,
    val rating: Double,
    val availability: Boolean,
)

data class Match(
    val id: String,
    val courtId: String,
    val date: String,
    val time: String,
    val durationMinutes: Int,
    val level: PadelLevel,
    val currentPlayers: List<Player>,
    val maxPlayers: Int = 4,
    val status: MatchStatus,
    val description: String? = null,
    val organizer: Player,
)
