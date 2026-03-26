package com.example.padel.data.model

/**
 * 与 Figma Make / 典型 REST JSON 对齐的字段（camelCase 便于日后接 Moshi/Gson）。
 * status: "open" | "full" | "completed"
 * level 文案与原型一致（西语），展示层可再映射为中文。
 */
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
    /** ISO-8601 日期 yyyy-MM-dd */
    val date: String,
    /** 如 18:00 */
    val time: String,
    val durationMinutes: Int,
    val level: PadelLevel,
    val currentPlayers: List<Player>,
    val maxPlayers: Int = 4,
    val status: MatchStatus,
    val description: String? = null,
    val organizer: Player,
)
