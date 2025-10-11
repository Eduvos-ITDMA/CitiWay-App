package com.example.citiway.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.citiway.data.local.entities.Route
import com.example.citiway.data.local.entities.Trip


// ==================== TRIPROUTE (Junction Table) ====================
@Entity(
    tableName = "triproute",
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["trip_id"],
            childColumns = ["trip_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Route::class,
            parentColumns = ["route_id"],
            childColumns = ["route_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TripRoute(
    @PrimaryKey(autoGenerate = true)
    val trip_route_id: Int = 0,
    val trip_id: Int? = null,
    val route_id: Int? = null,
    val fare_contribution: Double? = null
)