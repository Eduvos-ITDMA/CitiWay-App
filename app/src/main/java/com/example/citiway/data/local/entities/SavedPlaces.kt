package com.example.citiway.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This is your database table.
 * Every time a user clicks a prediction like "37 William Street",
 * you'll save it here so they can see recent searches later.
 */
@Entity(tableName = "recent_searches")
data class RecentSearch(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val placeId: String,          // The Google Place ID
    val placeName: String,        // e.g., "37 William Street"
    val placeAddress: String,     // e.g., "Observatory, Cape Town"
    val latitude: Double,         // For the map
    val longitude: Double,        // For the map
    val timestamp: Long = System.currentTimeMillis()
)