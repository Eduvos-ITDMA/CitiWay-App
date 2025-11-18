package com.example.citiway.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


// ==================== METRORAIL FARE ====================
@Entity(tableName = "metrorail_fare")
data class MetrorailFare(
    @PrimaryKey(autoGenerate = true)
    val metrorail_fare_id: Int = 0,
    val distance_band_lower_limit: Int,  // For distance-based lookup
    val zone: String? = null,
    val ticket_type: String? = null,
    val fare: Double? = null,
    val includes_return: Boolean? = null
)