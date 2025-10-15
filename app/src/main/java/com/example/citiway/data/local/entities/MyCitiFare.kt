package com.example.citiway.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


// ==================== MYCITI FARE ====================
@Entity(tableName = "myciti_fare")
data class MyCitiFare(
    @PrimaryKey(autoGenerate = true)
    val myciti_fare_id: Int = 0,
    val distance_band: String? = null,
    val peak_fare: Double? = null,
    val offpeak_fare: Double? = null
)