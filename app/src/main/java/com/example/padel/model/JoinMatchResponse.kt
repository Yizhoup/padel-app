package com.example.padel.model

data class JoinMatchResponse(
    val message: String,
    val match_id: Int,
    val plazas_restantes: String,
)
