package com.example.citiway.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Relation
import com.example.citiway.data.local.entities.CompletedJourneyEntity
import com.example.citiway.data.local.entities.InstructionEntity
import com.example.citiway.data.local.entities.StopEntity
import com.example.citiway.features.shared.Instruction
import com.example.citiway.features.shared.Journey
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
data class JourneyOverviewDb(
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
    @ColumnInfo(name = "is_favourite")
    val isFavourite: Boolean,

    @Relation(
        parentColumn = "journey_id",
        entityColumn = "journey_id"
    )
    val instructions: List<InstructionEntity>,
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

data class CompletedJourneyWithDetails(
    @Embedded
    val journey: CompletedJourneyEntity,

    @Relation(
        parentColumn = "journey_id",
        entityColumn = "journey_id"
    )
    val stops: List<StopEntity>,

    @Relation(
        parentColumn = "journey_id",
        entityColumn = "journey_id"
    )
    val instructions: List<InstructionEntity>
)