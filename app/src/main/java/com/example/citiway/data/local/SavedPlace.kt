package com.citiway.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_places")
data class SavedPlace(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val placeId: String,
    val placeName: String,
    val placeAddress: String,
    val latitude: Double,
    val longitude: Double,
    val isFavorite: Boolean = false,
    val lastUsedTimestamp: Long = System.currentTimeMillis()
)