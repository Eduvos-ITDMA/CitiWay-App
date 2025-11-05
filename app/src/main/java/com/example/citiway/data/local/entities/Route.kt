package com.example.citiway.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// ==================== ROUTE ====================
@Entity(
    tableName = "route",
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["trip_id"],
            childColumns = ["trip_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Provider::class,
            parentColumns = ["provider_id"],
            childColumns = ["provider_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MyCitiFare::class,
            parentColumns = ["myciti_fare_id"],
            childColumns = ["myciti_fare_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = MetrorailFare::class,
            parentColumns = ["metrorail_fare_id"],
            childColumns = ["metrorail_fare_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Route(
    @PrimaryKey(autoGenerate = true)
    val route_id: Int = 0,
    val trip_id: Int? = null,
    val provider_id: Int? = null,
    val start_location: String? = null,
    val destination: String? = null,
    val mode: String? = null,              // "bus", "train"
    val distance_km: Double? = null,
    val fare_contribution: Double? = null,
    val schedule: String? = null,
    val myciti_fare_id: Int? = null,
    val metrorail_fare_id: Int? = null,

    // NEW FIELDS for v1
    val vehicle_type: String? = null,      // "BUS", "HEAVY_RAIL", "WALK"
    val stop_count: Int? = null,           // Number of stops
    val duration_minutes: Int? = null      // Duration of this leg

    // route_order and route_name - skipping for now, add later when needed will use Journey table.
    //
)