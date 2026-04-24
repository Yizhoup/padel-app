package com.example.padel.model

data class PlayerCreateRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val nivel: String? = null,
    val ciudad: String? = null,
    val rol: String = "jugador",
)
