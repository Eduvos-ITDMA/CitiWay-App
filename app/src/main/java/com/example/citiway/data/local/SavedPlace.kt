package com.citiway.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_places")
data class SavedPlace(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // For place predictions ** Testing
    val placeId: String? = null,
    val placeName: String? = null,
    val placeAddress: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,

    // For completed journeys
    val route: String? = null,
    val journeyDate: String? = null,  // Store as "2025-12-25"
    val durationMin: Int? = null,

    // Shared fields
    val isFavorite: Boolean = false,
    val lastUsedTimestamp: Long = System.currentTimeMillis(),
    val itemType: String = "place"  // "place" or "journey"
)