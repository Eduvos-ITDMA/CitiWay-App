package com.example.citiway.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.citiway.data.local.Converters
import com.google.android.gms.maps.model.LatLng
import java.time.Instant

// ==================== COMPLETED JOURNEY ====================
@Entity(tableName = "completed_journey")
@TypeConverters(Converters::class)
data class CompletedJourneyEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "journey_id")
    val journeyId: Int = 0,
    @ColumnInfo(name = "user_id")
    val userId: Int,
    @ColumnInfo(name = "start_location_name")
    val startLocationName: String,
    @ColumnInfo(name = "destination_name")
    val destinationName: String,
    @ColumnInfo(name = "start_location_latlng")
    val startLocationLatLng: LatLng,
    @ColumnInfo(name = "destination_latlng")
    val destinationLatLng: LatLng,
    @ColumnInfo(name = "start_time")
    val startTime: Instant,
    @ColumnInfo(name = "arrival_time")
    val arrivalTime: Instant?,
    @ColumnInfo(name = "distance_meters")
    val distanceMeters: Int,
    @ColumnInfo(name = "fare_total")
    val fareTotal: Double,
    @ColumnInfo(name = "myciti_fare")
    val mycitiFare: Double,
    @ColumnInfo(name = "metrorail_fare")
    val metrorailFare: Double,
    @ColumnInfo(name = "total_stops_count")
    val totalStopsCount: Int = 0,
    @ColumnInfo(name = "is_favourite")
    val isFavourite: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: Long? = null
)