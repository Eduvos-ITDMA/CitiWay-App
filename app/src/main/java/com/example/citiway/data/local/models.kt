package com.example.citiway.data.local

import java.time.LocalDate

data class CompletedJourney(
    val id: String,
    val route: String,
    val date: String, // Should be a LocalDate in future... but made it temporary String for ease
    val durationMin: Int,
    val mode: String,
    val isFavourite: Boolean = false
)
