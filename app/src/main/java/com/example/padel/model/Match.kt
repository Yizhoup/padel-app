package com.example.padel.model

data class Match(
    val id: Int,
    val fecha: String,
    val hora: String,
    val ubicacion: String,
    val estado: String,
    val plazas_totales: Int = 4,
    val creador_id: Int? = null,
)
