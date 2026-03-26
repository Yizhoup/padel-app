package com.example.padel.data

import com.example.padel.data.model.Court
import com.example.padel.data.model.Match
import com.example.padel.data.model.Player

/**
 * 与后端 `/matches` 等资源对接时，在此实现网络数据源并保留相同返回结构。
 */
interface MatchRepository {
    fun getCourts(): List<Court>
    fun getCurrentUser(): Player
    /** 当前用户相关的比赛（组织者或已在 currentPlayers 中） */
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
