package com.example.padel.model

data class PlayerUpdateRequest(
    val nombre: String? = null,
    val email: String? = null,
    val password: String? = null,
    val nivel: String? = null,
    val ciudad: String? = null,
    val rol: String? = null,
)
