package com.example.citiway.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.citiway.data.local.entities.User

// ==================== TRIP ====================
@Entity(
    tableName = "trip",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Trip(
    @PrimaryKey(autoGenerate = true)
    val trip_id: Int = 0,
    val user_id: Int? = null,
    val start_stop: String? = null,
    val end_stop: String? = null,
    val date: String? = null,              // e.g., "2025-10-19"
    val trip_time: String? = null,         // e.g., "18min"
    val mode: String? = null,              // "Bus", "Train", "Multi"
    val total_distance_km: Double? = null,
    val total_fare: Double? = null,
    val is_favourite: Boolean = false,
    val created_at: Long? = null,  //Using system time now, so will have same time and when journey ends.

)