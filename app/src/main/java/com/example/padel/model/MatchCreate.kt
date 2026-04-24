package com.example.padel.model

data class MatchCreate(
    val fecha: String,
    val hora: String,
    val ubicacion: String,
    val nivel_requerido: String? = null,
    val plazas_totales: Int = 4,
    val descripcion: String? = null,
    val creador_id: Int,
)
