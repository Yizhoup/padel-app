package com.example.padel.model

data class PlayerProfile(
    val id: Int,
    val nombre: String,
    val email: String,
    val rol: String,
    val nivel: String? = null,
    val ciudad: String? = null,
)
