package com.example.citiway.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journeys")
data class Journey(
    @PrimaryKey(autoGenerate = true)
    val journey_id: Int = 0,

    val trip_id: Int,  // Foreign key to Trip table

    val start_time: String,  // ISO timestamp
    val arrival_time: String?,  // ISO timestamp
    val distance_meters: Int,
    val total_stops_count: Int,

    val total_walk_distance_meters: Int = 0,  // Added total walking distance better to store than working out each time from steps.

    // Store the actual coordinates used for routing - satisfies JourneyOptions
    val start_lat: Double,
    val start_lng: Double,
    val dest_lat: Double,
    val dest_lng: Double,

    val created_at: Long = System.currentTimeMillis()
)