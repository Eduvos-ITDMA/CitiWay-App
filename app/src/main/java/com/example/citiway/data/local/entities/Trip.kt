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
    val start_stop: String? = null,  //made it strings (no offline stop id's)
    val end_stop: String? = null,
    val date: String? = null,
    val trip_time: String? = null,
    val total_fare: Double? = null
)