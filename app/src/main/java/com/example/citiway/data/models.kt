package com.example.citiway.data

import java.time.LocalDate

data class CompletedJourney(
    val id: String,
    val route: String,
    val date: LocalDate,
    val durationMin: Int,
    val isFavourite: Boolean = false
)
