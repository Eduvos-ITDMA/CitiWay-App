package com.example.citiway.data.local

import androidx.room.ColumnInfo
import com.example.citiway.features.shared.Instruction
import com.example.citiway.features.shared.Journey
import com.example.citiway.features.shared.Stop
import com.google.android.gms.maps.model.LatLng
import java.time.Instant

data class JourneyOverview(
    val id: Int,
    val route: String,
    val startLocationLatLng: LatLng,
    val destinationLatLng: LatLng,
    val date: String, // Should be a LocalDate in future... but made it temporary String for ease
    val durationMin: Int,
    val mode: String,
    val isFavourite: Boolean = false
)

// DTO for retrieving only the necessary data from the DB to create a JourneyOverview object
data class JourneyOverviewDTO(
    val journey_id: Int = 0,
    val user_id: Int,
    val start_location_name: String,
    val destination_name: String,
    val start_location_latlng: LatLng,
    val destination_latlng: LatLng,
    val start_time: Instant,
    val arrival_time: Instant?,
    val instructions: List<Instruction>,
    val is_favourite: Boolean
)

data class CompletedJourney(
    val journeyId: Int = 0,
    val userId: Int,
    val startLocationName: String,
    val destinationName: String,
    val startLocationLatLng: LatLng,
    val destinationLatLng: LatLng,
    val journey: Journey,
    val isFavourite: Boolean = false,
    val createdAt: Long? = null
)

data class JourneyDb(
    @ColumnInfo(name = "stops")
    val stops: List<Stop>,

    @ColumnInfo(name = "instructions")
    val instructions: List<Instruction>,

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
    val totalStopsCount: Int = 0
)