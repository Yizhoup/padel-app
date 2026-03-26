package com.example.padel.model

data class MatchCreate(
    val is_1v1: Boolean,
    val is_private: Boolean,
    val host_id: Int? = null,
    val programmed_date: String
)