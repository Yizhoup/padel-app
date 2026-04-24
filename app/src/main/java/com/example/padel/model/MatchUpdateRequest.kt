package com.example.padel.model

data class MatchUpdateRequest(
    val fecha: String? = null,
    val hora: String? = null,
    val ubicacion: String? = null,
    val nivel_requerido: String? = null,
    val plazas_totales: Int? = null,
    val estado: String? = null,
    val descripcion: String? = null,
)
