package com.example.citiway.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// ==================== ROUTE ====================
@Entity(
    tableName = "route",
    foreignKeys = [
        ForeignKey(
            entity = Provider::class,
            parentColumns = ["provider_id"],
            childColumns = ["provider_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Route(
    @PrimaryKey(autoGenerate = true)
    val route_id: Int = 0,
    val provider_id: Int? = null,
    val start_location: String? = null,
    val destination: String? = null,
    val mode: String? = null,
    val base_fare: Double? = null,
    val schedule: String? = null
)