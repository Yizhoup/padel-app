package com.example.padel.data

import com.example.padel.data.model.Court
import com.example.padel.data.model.Match
import com.example.padel.data.model.MatchStatus
import com.example.padel.data.model.PadelLevel
import com.example.padel.data.model.Player

/**
 * 与 Figma Make mockData 同步的示例数据，便于联调与 UI 预览。
 */
object PadelSampleData {

    val players: List<Player> = listOf(
        Player("1", "Carlos Rodríguez", null, PadelLevel.AVANZADO, 4.5, 45),
        Player("2", "María García", null, PadelLevel.INTERMEDIO, 3.8, 32),
        Player("3", "Juan Martínez", null, PadelLevel.PROFESIONAL, 4.9, 120),
        Player("4", "Sofía López", null, PadelLevel.PRINCIPIANTE, 2.5, 8),
    )

    val courts: List<Court> = listOf(
        Court(
            id = "1",
            name = "Club Padel Premium",
            address = "Calle Mayor 123, Madrid",
            imageUrl = "https://images.unsplash.com/photo-1658491830143-72808ca237e3",
            pricePerHour = 35,
            indoor = true,
            amenities = listOf("Aparcamiento", "Vestuarios", "Tienda", "Cafetería"),
            rating = 4.8,
            availability = true,
        ),
        Court(
            id = "2",
            name = "Padel Center Barcelona",
            address = "Avenida Diagonal 456, Barcelona",
            imageUrl = "https://images.unsplash.com/photo-1703152838237-6deac56a89ec",
            pricePerHour = 40,
            indoor = false,
            amenities = listOf("Aparcamiento", "Restaurante", "Clases"),
            rating = 4.6,
            availability = true,
        ),
        Court(
            id = "3",
            name = "Valencia Padel Club",
            address = "Calle del Mar 789, Valencia",
            imageUrl = "https://images.unsplash.com/photo-1771909720886-a90afd1b37f5",
            pricePerHour = 30,
            indoor = true,
            amenities = listOf("Aparcamiento", "Vestuarios", "Sauna"),
            rating = 4.7,
            availability = false,
        ),
        Court(
            id = "4",
            name = "Complejo Deportivo Sevilla",
            address = "Plaza de España 101, Sevilla",
            imageUrl = "https://images.unsplash.com/photo-1709587823868-735f9375ae74",
            pricePerHour = 28,
            indoor = false,
            amenities = listOf("Aparcamiento", "Tienda"),
            rating = 4.5,
            availability = true,
        ),
    )

    val currentUser: Player = Player(
        id = "current-user",
        name = "Tú",
        null,
        PadelLevel.INTERMEDIO,
        3.5,
        23,
    )

    val allMatches: List<Match> = listOf(
        Match(
            id = "1",
            courtId = "1",
            date = "2026-03-26",
            time = "18:00",
            durationMinutes = 90,
            level = PadelLevel.INTERMEDIO,
            currentPlayers = listOf(currentUser, players[1]),
            maxPlayers = 4,
            status = MatchStatus.OPEN,
            description = "¡Buscamos 2 jugadores más para un partido divertido!",
            organizer = currentUser,
        ),
        Match(
            id = "2",
            courtId = "2",
            date = "2026-03-27",
            time = "10:00",
            durationMinutes = 120,
            level = PadelLevel.AVANZADO,
            currentPlayers = listOf(players[2], currentUser),
            maxPlayers = 4,
            status = MatchStatus.OPEN,
            description = "Partido competitivo, solo jugadores avanzados",
            organizer = players[2],
        ),
        Match(
            id = "3",
            courtId = "1",
            date = "2026-03-28",
            time = "19:30",
            durationMinutes = 90,
            level = PadelLevel.TODOS_LOS_NIVELES,
            currentPlayers = listOf(currentUser, players[1], players[3]),
            maxPlayers = 4,
            status = MatchStatus.OPEN,
            description = "Partido casual por la tarde, todos bienvenidos",
            organizer = currentUser,
        ),
        Match(
            id = "4",
            courtId = "4",
            date = "2026-03-29",
            time = "17:00",
            durationMinutes = 90,
            level = PadelLevel.PRINCIPIANTE,
            currentPlayers = listOf(players[3]),
            maxPlayers = 4,
            status = MatchStatus.OPEN,
            description = "Perfecto para aprender y mejorar",
            organizer = players[3],
        ),
        Match(
            id = "5",
            courtId = "2",
            date = "2026-03-26",
            time = "20:00",
            durationMinutes = 90,
            level = PadelLevel.PROFESIONAL,
            currentPlayers = listOf(players[2], players[0], players[1], currentUser),
            maxPlayers = 4,
            status = MatchStatus.FULL,
            description = "Sesión de práctica para torneo",
            organizer = players[2],
        ),
        Match(
            id = "6",
            courtId = "3",
            date = "2026-03-20",
            time = "16:00",
            durationMinutes = 90,
            level = PadelLevel.INTERMEDIO,
            currentPlayers = listOf(players[0], players[1], players[2], currentUser),
            maxPlayers = 4,
            status = MatchStatus.COMPLETED,
            description = null,
            organizer = players[1],
        ),
    )
}
