package com.example.padel.data

import com.example.padel.data.model.Court
import com.example.padel.data.model.Match
import com.example.padel.data.model.Player

interface MatchRepository {
    fun getCourts(): List<Court>
    fun getCurrentUser(): Player
    fun getMyMatches(): List<Match>
}

class SampleMatchRepository(
    private val allMatches: List<Match> = PadelSampleData.allMatches,
    private val currentUser: Player = PadelSampleData.currentUser,
) : MatchRepository {
    override fun getCourts(): List<Court> = PadelSampleData.courts

    override fun getCurrentUser(): Player = currentUser

    override fun getMyMatches(): List<Match> {
        val uid = currentUser.id
        return allMatches.filter { match ->
            match.organizer.id == uid ||
                match.currentPlayers.any { it.id == uid }
        }
    }
}
